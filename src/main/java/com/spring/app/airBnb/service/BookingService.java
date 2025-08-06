package com.spring.app.airBnb.service;

import com.spring.app.airBnb.dto.BookingDto;
import com.spring.app.airBnb.dto.BookingRequest;
import com.spring.app.airBnb.dto.GuestDto;
import com.spring.app.airBnb.dto.HotelReportDto;
import com.spring.app.airBnb.entity.Booking;
import com.stripe.model.Event;

import java.nio.file.AccessDeniedException;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface BookingService {

    BookingDto initializeBooking(BookingRequest request);

    BookingDto addGuests(Long bookingId, List<GuestDto> guestDtoList);

    String initiatePayments(Long bookingId);

    void capturePayment(Event event);

    void cancelBooking(Long bookingId);

    String getBookingStatus(Long bookingId);

    List<BookingDto> getAllHotelBookings(Long hotelId) throws AccessDeniedException;

    HotelReportDto getHotelReport(Long hotelId,LocalDate startDate, LocalDate endDate) throws AccessDeniedException;

    List<BookingDto> getAllUserBookings();
}
