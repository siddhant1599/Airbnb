package com.spring.app.airBnb.service;

import com.spring.app.airBnb.dto.RoomDto;
import com.spring.app.airBnb.entity.Hotel;
import com.spring.app.airBnb.entity.Room;
import com.spring.app.airBnb.exception.ResourceNotFoundException;
import com.spring.app.airBnb.repository.HotelRepository;
import com.spring.app.airBnb.repository.RoomRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class RoomServiceImpl implements RoomService{

    private final RoomRepository roomRepository;
    private final ModelMapper modelMapper;
    private final HotelRepository hotelRepository;
    private final InventoryService inventoryService;


    @Override
    @Transactional
    public RoomDto createNewRoom(RoomDto roomDto, Long hotelId) {

        Hotel hotel = hotelRepository.findById(hotelId).orElseThrow( () -> {
            return new ResourceNotFoundException("Hotel not found by id " + hotelId);
        });
        log.info("Creating a new Room with id : {}", roomDto.getId());
        Room room = modelMapper.map(roomDto, Room.class);
        room.setHotel(hotel);

        room = roomRepository.save(room);
        log.info("Created a new Room with id : {}", roomDto.getId());

        if(hotel.getActive() == true){
            inventoryService.initializeRoomForYear(room);
        }

        return modelMapper.map(room, RoomDto.class);
    }

    @Override
    public List<RoomDto> getAllRoomsInHotel(Long hotelId) {

        Hotel hotel = hotelRepository.findById(hotelId).orElseThrow( () -> {
            return new ResourceNotFoundException("Hotel not found by id " + hotelId);
        });

        List<RoomDto> roomDtoList = new ArrayList<>();
        List<Room> rooms = hotel.getRooms();

        rooms.forEach((room -> {
            roomDtoList.add(modelMapper.map(room, RoomDto.class));
        }));

        return roomDtoList;
    }

    @Override
    public RoomDto getRoomById(Long roomId) {
        Room room = roomRepository.findById(roomId).orElseThrow( () ->
                new ResourceNotFoundException("Room does not exist ith id -" + roomId)
        );

        return modelMapper.map(room, RoomDto.class);
    }

    @Override
    @Transactional
    public void deleteRoomnById(Long roomId) {
        log.info("Deleting a Room with id : {}", roomId);

        Room room = roomRepository.findById(roomId).orElseThrow( () ->
                new ResourceNotFoundException("Room does not exist ith id -" + roomId)
        );

        inventoryService.deleteFutureInventory(room);
        roomRepository.deleteById(roomId);

        log.info("Deleted a Room with id : {}", roomId);
    }
}
