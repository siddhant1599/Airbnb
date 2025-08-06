package com.spring.app.airBnb.controller;

import com.spring.app.airBnb.dto.BookingDto;
import com.spring.app.airBnb.dto.BookingRequest;
import com.spring.app.airBnb.dto.GuestDto;
import com.spring.app.airBnb.service.BookingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

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

    @PostMapping("/{bookingId}/payments")
    public ResponseEntity<Map<String, String>> initiatePayment(@PathVariable Long bookingId){

        String sessionUrl = bookingService.initiatePayments(bookingId);
        return ResponseEntity.ok(Map.of("sessionUrl", sessionUrl));
    }

    @PostMapping("/{bookingId}/cancel")
    public ResponseEntity<Void> cancelBooking(@PathVariable Long bookingId){

        bookingService.cancelBooking(bookingId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{bookingId}/status")
    public ResponseEntity<Map<String, String>> getBookingStatus(@PathVariable Long bookingId){
        return ResponseEntity.ok(Map.of("status" , bookingService.getBookingStatus(bookingId)));
    }
}
