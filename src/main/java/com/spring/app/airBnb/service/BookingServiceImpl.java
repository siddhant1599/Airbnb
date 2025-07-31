package com.spring.app.airBnb.service;

import com.spring.app.airBnb.dto.BookingDto;
import com.spring.app.airBnb.dto.BookingRequest;
import com.spring.app.airBnb.dto.GuestDto;
import com.spring.app.airBnb.entity.*;
import com.spring.app.airBnb.entity.enums.BookingStatus;
import com.spring.app.airBnb.exception.ResourceNotFoundException;
import com.spring.app.airBnb.exception.UnauthorizedException;
import com.spring.app.airBnb.repository.*;
import com.spring.app.airBnb.strategy.PricingService;
import com.stripe.exception.StripeException;
import com.stripe.model.Event;
import com.stripe.model.Refund;
import com.stripe.model.checkout.Session;
import com.stripe.param.RefundCreateParams;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class BookingServiceImpl implements BookingService{

    private final BookingRepository bookingRepository;
    private final RoomRepository roomRepository;
    private final HotelRepository hotelRepository;
    private final InventoryRepository inventoryRepository;
    private final GuestRepository guestRepository;
    private final CheckoutService checkoutService;
    private final ModelMapper modelMapper;
    private final PricingService pricingService;

    @Value("${frontend.url}")
    private String frontendUrl;



    @Override
    @Transactional
    public BookingDto initializeBooking(BookingRequest request) {

        log.info("Initializing booking for hotel {}, room {}, date {} {}",
                request.getHotelId(),
                request.getRoomId(),
                request.getCheckInDate(),
                request.getCheckOutDate());


        Hotel hotel = hotelRepository.findById(request.getHotelId()).orElseThrow( () ->
             new ResourceNotFoundException("Hotel not found by id " + request.getHotelId())
        );
        Room room = roomRepository.findById(request.getRoomId()).orElseThrow( () ->
                new ResourceNotFoundException("Room does not exist ith id -" + request.getRoomId())
        );


        List<Inventory> inventoryList = inventoryRepository.findAndLockAvailableInventory(
                request.getRoomId(), request.getCheckInDate(),request.getCheckOutDate(), request.getRoomsCount()
        );
        long daysCount = ChronoUnit.DAYS.between(request.getCheckInDate(), request.getCheckOutDate()) + 1;
        if(inventoryList.size() != daysCount){
            throw new IllegalStateException(" Room is not available anymore");
        }

        //Reserve the room/ update the booked count of inventories

        inventoryRepository.initBooking(room.getId(), request.getCheckOutDate(), request.getCheckOutDate(), request.getRoomsCount());


        BigDecimal priceForOneRoom = pricingService.calculateTotalPrice(inventoryList);
        BigDecimal totalprice = priceForOneRoom.multiply(BigDecimal.valueOf(request.getRoomsCount()));

        Booking booking = Booking.builder()
                .bookingStatus(BookingStatus.RESERVED)
                .hotel(hotel)
                .room(room)
                .user(getCurrentUser())
                .checkInDate(request.getCheckInDate())
                .checkOutDate(request.getCheckOutDate())
                .roomCount(request.getRoomsCount())
                .amount(totalprice)
                .build();

        Booking booking1 = bookingRepository.save(booking);
        return modelMapper.map(booking1, BookingDto.class);
    }

    @Override
    @Transactional
    public String initiatePayments(Long bookingId) {

        Booking booking = bookingRepository.findById(bookingId).orElseThrow(
                () ->  new ResourceNotFoundException("Booking not found by id : " + bookingId)
        );

        User currentUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if(!booking.getUser().equals(currentUser)){
                throw new UnauthorizedException("User does not contain booking with id:" + bookingId);
        }

        if(hasBookingExpired(booking)){
            throw new IllegalStateException("Booking has already expired with id - " + bookingId);
        }

       String sessionUrl = checkoutService.getCheckoutSession(booking, frontendUrl+"/payments/success", frontendUrl+"/payments/failure");

        booking.setBookingStatus(BookingStatus.PAYMENT_PENDING);
        bookingRepository.save(booking);

        return sessionUrl;
    }

    @Override
    @Transactional
    public void capturePayment(Event event) {
        if("checkout.session.completed".equals(event.getType())){

            Session session = (Session)event.getDataObjectDeserializer().getObject().orElse(null);
            if(session == null) return;

            String sessionId = session.getId();
            Booking booking = bookingRepository.findByPaymentSessionId(sessionId).orElseThrow(
                    () -> new ResourceNotFoundException("Booking not found for session : " + sessionId)
            );

            booking.setBookingStatus(BookingStatus.CONFIRMED);
            bookingRepository.save(booking);

            inventoryRepository.findAndLockReservedInventory(booking.getRoom().getId(), booking.getCheckInDate(),booking.getCheckOutDate(), booking.getRoomCount());
            inventoryRepository.confirmBooking(booking.getRoom().getId(), booking.getCheckInDate(), booking.getCheckOutDate(), booking.getRoomCount());

            log.info("Successfully confirmed the booking for booking id : {}", booking.getId());
        }
        else{
            log.warn("Unhandled event type : {}", event.getType());
        }

    }

    @Override
    @Transactional
    public void cancelBooking(Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(
                () ->  new ResourceNotFoundException("Booking not found by id : " + bookingId)
        );

        User currentUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if(!booking.getUser().equals(currentUser)){
            throw new UnauthorizedException("User does not contain booking with id:" + bookingId);
        }

        if(booking.getBookingStatus() != BookingStatus.CONFIRMED){
            throw new IllegalStateException("Booking is not in confirmed status with id - " + bookingId);
        }

        booking.setBookingStatus(BookingStatus.CANCELLED);
        bookingRepository.save(booking);

        inventoryRepository.findAndLockReservedInventory(booking.getRoom().getId(), booking.getCheckInDate(),booking.getCheckOutDate(), booking.getRoomCount());
        inventoryRepository.cancelBooking(booking.getRoom().getId(), booking.getCheckInDate(), booking.getCheckOutDate(), booking.getRoomCount());

        // handle the refund

        try{
            Session session = Session.retrieve(booking.getPaymentSessionId());
            RefundCreateParams refundCreateParams = RefundCreateParams.builder()
                    .setPaymentIntent(session.getPaymentIntent())
                    .build();

            Refund.create(refundCreateParams);

        } catch (StripeException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public BookingDto addGuests(Long bookingId, List<GuestDto> guestDtoList) {

        log.info("Adding guests for booking with id : {}", bookingId);

        User currentuser = getCurrentUser();
        Booking booking = bookingRepository.findById(bookingId).orElseThrow( () ->
                new ResourceNotFoundException("Booking not found by id " + bookingId)
        );

        if(!booking.getUser().equals(currentuser)){
            throw new UnauthorizedException("User does not contain booking with id:" + bookingId);
        }

        if(hasBookingExpired(booking)){
            throw new IllegalStateException("Booking has already expired : bookingId - " + booking.getId());
        }
        if(booking.getBookingStatus() != BookingStatus.RESERVED){
            throw new IllegalStateException("Booking is not under reserved state : bookingId -" + booking.getId());
        }

        booking.setGuests(guestDtoList.stream().map((element) -> {

            Guest guest = modelMapper.map(element, Guest.class);
            guest.setUser(currentuser);
            return guestRepository.save(guest);
        }).collect(Collectors.toSet())
        );

        booking.setBookingStatus(BookingStatus.GUESTS_ADDED);
        Booking booking1 = bookingRepository.save(booking);
        log.info("Added guests for booking with id : {}", bookingId);

        return modelMapper.map(booking1, BookingDto.class);
    }

    private boolean hasBookingExpired(Booking booking){
        return booking.getCreatedAt().plusMinutes(10).isBefore(LocalDateTime.now());
    }

    private User getCurrentUser(){
        return (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }
}
