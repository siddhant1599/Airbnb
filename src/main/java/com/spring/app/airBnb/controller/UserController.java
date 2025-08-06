package com.spring.app.airBnb.controller;

import com.spring.app.airBnb.dto.BookingDto;
import com.spring.app.airBnb.dto.ProfileUpdateRequestDto;
import com.spring.app.airBnb.dto.UserDto;
import com.spring.app.airBnb.entity.Booking;
import com.spring.app.airBnb.service.BookingService;
import com.spring.app.airBnb.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final BookingService bookingService;

    @PatchMapping("/profile")
    public ResponseEntity<Void> updateProfile(@RequestBody ProfileUpdateRequestDto requestDto){

        userService.updateProfile(requestDto);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/bookings")
    public ResponseEntity<List<BookingDto>> getUserBookings(){
        List<BookingDto> userBookings = bookingService.getAllUserBookings();
        return ResponseEntity.ok(userBookings);
    }

    @GetMapping("/profile")
    public ResponseEntity<UserDto> getUserProfile(){
        return ResponseEntity.ok(userService.getUserProfile());
    }

}
