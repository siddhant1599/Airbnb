package com.spring.app.airBnb.controller;

import com.spring.app.airBnb.dto.BookingDto;
import com.spring.app.airBnb.dto.BookingRequest;
import com.spring.app.airBnb.dto.GuestDto;
import com.spring.app.airBnb.service.BookingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/bookings")
public class HotelBookingController {

    private final BookingService bookingService;

    @PostMapping("/init")
    public ResponseEntity<BookingDto> initializeBooking(@RequestBody BookingRequest request){

        return ResponseEntity.ok(bookingService.initializeBooking(request));
    }


    @PostMapping("/{bookingId}/addGuests")
    public ResponseEntity<BookingDto> addGuests(@RequestBody List<GuestDto> guestDtos, @PathVariable Long bookingId){

        return ResponseEntity.ok(bookingService.addGuests(bookingId, guestDtos));
    }
}
