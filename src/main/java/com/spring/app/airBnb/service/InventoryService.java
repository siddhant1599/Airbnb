package com.spring.app.airBnb.service;

import com.spring.app.airBnb.dto.HotelDto;
import com.spring.app.airBnb.dto.HotelSearchRequest;
import com.spring.app.airBnb.entity.Room;
import org.springframework.data.domain.Page;

public interface InventoryService {

    void initializeRoomForYear(Room room);

    void deleteByRoom (Room room);

    Page<HotelDto> searchHotels(HotelSearchRequest hotelSearchRequest);
}
