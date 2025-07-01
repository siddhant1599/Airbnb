package com.spring.app.airBnb.dto;

import com.spring.app.airBnb.entity.Hotel;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class HotelPriceDto {

    private Hotel hotel;

    private Double price;
}
