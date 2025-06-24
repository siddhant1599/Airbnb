package com.spring.app.airBnb.service;

import com.spring.app.airBnb.dto.BookingDto;
import com.spring.app.airBnb.dto.BookingRequest;
import com.spring.app.airBnb.dto.GuestDto;
import com.spring.app.airBnb.entity.Booking;

import java.util.List;

public interface BookingService {

    BookingDto initializeBooking(BookingRequest request);

    BookingDto addGuests(Long bookingId, List<GuestDto> guestDtoList);
}
