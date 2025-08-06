package com.spring.app.airBnb.service;

import com.spring.app.airBnb.dto.HotelDto;
import com.spring.app.airBnb.dto.HotelInfoDto;
import com.spring.app.airBnb.dto.RoomDto;
import com.spring.app.airBnb.entity.Hotel;
import com.spring.app.airBnb.entity.Room;
import com.spring.app.airBnb.entity.User;
import com.spring.app.airBnb.exception.ResourceNotFoundException;
import com.spring.app.airBnb.exception.UnauthorizedException;
import com.spring.app.airBnb.repository.HotelRepository;
import com.spring.app.airBnb.repository.RoomRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

import static com.spring.app.airBnb.util.AppUtils.getCurrentUser;

@Service
@RequiredArgsConstructor
@Slf4j
public class HotelServiceImpl implements HotelService {

    private final HotelRepository hotelRepository;
    private final ModelMapper modelMapper;
    private final InventoryService inventoryService;
    private final RoomRepository  roomRepository;


    @Override
    public HotelDto createNewHotel(HotelDto hotelDto) {
        log.info("Creating a new Hotel with name : {}", hotelDto.getName());
        Hotel hotel = modelMapper.map(hotelDto,Hotel.class);
        hotel.setActive(false);

        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        hotel.setOwner(user);

        hotel = hotelRepository.save(hotel);
        log.info("Created a new Hotel with name : {}", hotel.getName());
        return modelMapper.map(hotel, HotelDto.class);
    }

    @Override
    public HotelDto getHotelById(Long id) {
        log.info("Getting the hotel by id {}", id);
        Hotel hotel = hotelRepository.findById(id).orElseThrow(
                () ->  new ResourceNotFoundException("Hotel not found by id :" + id)
        );

        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if(!user.equals(hotel.getOwner())){
            throw new UnauthorizedException("current user does not own the hotel with id : " + id);
        }
        return modelMapper.map(hotel, HotelDto.class);
    }

    @Override
    public List<HotelDto> getAllHotels() {

        User user = getCurrentUser();
        log.info("Getting all hotels for the admin user : {}" ,user.getName());
        List<Hotel> hotelList = hotelRepository.findByOwner(user);
        List<HotelDto> hotelDtoList = new ArrayList<>();

        hotelList.forEach(hotel -> {
            hotelDtoList.add(modelMapper.map(hotel, HotelDto.class));
        });

        return hotelDtoList;
    }

    @Override
    public HotelDto updateHotelById(HotelDto hotelDto, Long id) {
        log.info("Updating  hotel with ID : {}", id);

        Hotel hotel = hotelRepository.findById(id).orElseThrow( () -> {
            return new ResourceNotFoundException("Hotel not found by id " + id);
        });

        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if(!user.equals(hotel.getOwner())){
            throw new UnauthorizedException("current user does not own the hotel with id : " + id);
        }

        modelMapper.map(hotelDto, hotel);
        hotel.setId(id);
        hotelRepository.save(hotel);

        log.info("Updated  hotel with ID : {}", id);
        return modelMapper.map(hotel, HotelDto.class);
    }

    @Override
    @Transactional
    public void deleteHotelById(Long id) {

        log.info("Deleting  hotel with ID : {}", id);

        Hotel hotel = hotelRepository.findById(id).orElseThrow( () -> {
            return new ResourceNotFoundException("Hotel not found by id " + id);
        });

        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if(!user.equals(hotel.getOwner())){
            throw new UnauthorizedException("current user does not own the hotel with id : " + id);
        }

        for(Room room : hotel.getRooms()){
            inventoryService.deleteByRoom(room);
            roomRepository.deleteById(room.getId());
        }

        hotelRepository.deleteById(id);

        log.info("Deleted  hotel with ID : {}", id);
    }

    @Override
    @Transactional
    public HotelDto activateHotelStatus(Long id) {

        Hotel hotel = hotelRepository.findById(id).orElseThrow( () -> {
            return new ResourceNotFoundException("Hotel not found by id " + id);
        });

        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if(!user.equals(hotel.getOwner())){
            throw new UnauthorizedException("current user does not own the hotel with id : " + id);
        }

        hotel.setActive(true);
        hotelRepository.save(hotel);
        log.info("hotel with ID : {} is set to status {}", id, true);

        for (Room room : hotel.getRooms()){
            inventoryService.initializeRoomForYear(room);
        }

        return modelMapper.map(hotel,HotelDto.class);

    }

    @Override
    public HotelInfoDto getHotelInfoById(Long hotelId) {

        Hotel hotel = hotelRepository.findById(hotelId).orElseThrow( () -> {
            return new ResourceNotFoundException("Hotel not found by id " + hotelId);
        });

        List<RoomDto> roomDtos = hotel.getRooms().stream()
                .map(room -> modelMapper.map(room, RoomDto.class))
                .toList();

        return new HotelInfoDto(modelMapper.map(hotel, HotelDto.class), roomDtos);

    }

}
