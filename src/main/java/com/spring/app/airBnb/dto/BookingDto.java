package com.spring.app.airBnb.dto;

import com.spring.app.airBnb.entity.*;
import com.spring.app.airBnb.entity.enums.BookingStatus;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;

@Data
public class BookingDto {

    private Long id;
    private Integer roomCount;
    private LocalDate checkOutDate;
    private LocalDate checkInDate;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private BookingStatus bookingStatus;
    private Set<GuestDto> guests;

}
