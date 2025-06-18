package com.spring.app.airBnb.controller;

import com.spring.app.airBnb.dto.RoomDto;
import com.spring.app.airBnb.service.RoomService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/hotels/{hotelId}/rooms")
@RequiredArgsConstructor
@Slf4j
public class RoomController {

    private final RoomService roomService;


    @PostMapping()
    public ResponseEntity<RoomDto> createNewRoom(@RequestBody RoomDto roomDto, @PathVariable Long hotelId){
        RoomDto roomDto1 = roomService.createNewRoom(roomDto, hotelId);

        return new ResponseEntity(roomDto1 , HttpStatus.CREATED);
    }

    @GetMapping("/{roomId}")
    public ResponseEntity<RoomDto> getRoomById(@PathVariable Long roomId){

        RoomDto roomDto = roomService.getRoomById(roomId);

        return ResponseEntity.ok(roomDto);

    }

    @GetMapping
    public ResponseEntity<List<RoomDto>> getAllRooms(@PathVariable Long hotelId){

        List<RoomDto> roomDtoList = roomService.getAllRoomsInHotel(hotelId);

        return ResponseEntity.ok(roomDtoList);
    }

    @DeleteMapping("/{roomId}")
    public ResponseEntity<RoomDto> deleteRoomById(@PathVariable Long roomId){

        roomService.deleteRoomnById(roomId);

        return ResponseEntity.noContent().build();
    }




}
