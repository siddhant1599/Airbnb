package com.spring.app.airBnb.service;

import com.spring.app.airBnb.dto.RoomDto;

import java.util.List;

public interface RoomService {

    RoomDto createNewRoom(RoomDto roomDto, Long hotelId);

    List<RoomDto> getAllRoomsInHotel(Long hotelId);

    RoomDto getRoomById(Long roomId);

    void deleteRoomnById(Long roomId);

    RoomDto updateRoomById(RoomDto roomDto, Long roomId, Long hotelId);
}
