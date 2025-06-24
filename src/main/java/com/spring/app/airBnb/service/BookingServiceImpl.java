package com.spring.app.airBnb.service;

import com.spring.app.airBnb.dto.BookingDto;
import com.spring.app.airBnb.dto.BookingRequest;
import com.spring.app.airBnb.dto.GuestDto;
import com.spring.app.airBnb.entity.*;
import com.spring.app.airBnb.entity.enums.BookingStatus;
import com.spring.app.airBnb.exception.ResourceNotFoundException;
import com.spring.app.airBnb.repository.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
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
    private final ModelMapper modelMapper;



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

        for(Inventory inventory : inventoryList){
            inventory.setReservedCount(inventory.getReservedCount() + request.getRoomsCount());
        }

        inventoryRepository.saveAll(inventoryList);

        //TODO : calculate dynamic price

        Booking booking = Booking.builder()
                .bookingStatus(BookingStatus.RESERVED)
                .hotel(hotel)
                .room(room)
                .user(getCurrentUser())
                .checkInDate(request.getCheckInDate())
                .checkOutDate(request.getCheckOutDate())
                .roomCount(request.getRoomsCount())
                .amount(BigDecimal.TEN)
                .build();

        Booking booking1 = bookingRepository.save(booking);
        return modelMapper.map(booking1, BookingDto.class);
    }

    @Override
    public BookingDto addGuests(Long bookingId, List<GuestDto> guestDtoList) {

        Booking booking = bookingRepository.findById(bookingId).orElseThrow( () ->
                new ResourceNotFoundException("Booking not found by id " + bookingId)
        );

        if(hasBookingExpired(booking)){
            throw new IllegalStateException("Booking has already expired : bookingId - " + booking.getId());
        }
        if(booking.getBookingStatus() != BookingStatus.RESERVED){
            throw new IllegalStateException("Booking is not under reserved state : bookingId -" + booking.getId());
        }

        booking.setGuests(guestDtoList.stream().map((element) -> {

            Guest guest = modelMapper.map(element, Guest.class);
            guest.setUser(getCurrentUser());
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
        User user = new User();
        user.setId(2L);
        return user;
    }
}
