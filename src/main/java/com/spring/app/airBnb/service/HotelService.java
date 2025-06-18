package com.spring.app.airBnb.service;

import com.spring.app.airBnb.dto.HotelDto;

import java.util.List;

public interface HotelService {

    HotelDto createNewHotel(HotelDto hotelDto);

    HotelDto getHotelById(Long id);

    List<HotelDto> getAllHotels();

    HotelDto updateHotelById(HotelDto hotelDto, Long id);

    void deleteHotelById(Long id);

    HotelDto activateHotelStatus(Long id);
}
