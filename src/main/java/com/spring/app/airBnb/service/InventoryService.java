package com.spring.app.airBnb.service;

import java.util.*;
import com.spring.app.airBnb.dto.HotelPriceDto;
import com.spring.app.airBnb.dto.HotelSearchRequest;
import com.spring.app.airBnb.dto.InventoryDto;
import com.spring.app.airBnb.dto.UpdateInventoryRequestDto;
import com.spring.app.airBnb.entity.Room;
import org.springframework.data.domain.Page;

public interface InventoryService {

    void initializeRoomForYear(Room room);

    void deleteByRoom (Room room);

    Page<HotelPriceDto> searchHotels(HotelSearchRequest hotelSearchRequest);

    List<InventoryDto> getAllInventoryByRoom(Long roomId);

    void updateInventory(Long roomsId, UpdateInventoryRequestDto requestDto);
}
