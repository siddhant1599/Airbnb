package com.spring.app.airBnb.dto;

import com.spring.app.airBnb.entity.Hotel;
import com.spring.app.airBnb.entity.Room;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@RequiredArgsConstructor
@Builder
public class InventoryDto {

    private Long id;
    private LocalDate date;
    private Integer bookedCount;
    private Integer reservedCount;
    private Integer totalCount;
    private BigDecimal surgeFactor;
    private BigDecimal price;
    private Boolean closed;

}
