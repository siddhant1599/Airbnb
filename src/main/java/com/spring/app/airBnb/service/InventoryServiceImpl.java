package com.spring.app.airBnb.service;

import com.spring.app.airBnb.dto.*;
import com.spring.app.airBnb.entity.Inventory;
import com.spring.app.airBnb.entity.Room;
import com.spring.app.airBnb.entity.User;
import com.spring.app.airBnb.exception.ResourceNotFoundException;
import com.spring.app.airBnb.repository.HotelMinPriceRepository;
import com.spring.app.airBnb.repository.InventoryRepository;
import com.spring.app.airBnb.repository.RoomRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

import static com.spring.app.airBnb.util.AppUtils.getCurrentUser;

@Service
@Slf4j
@RequiredArgsConstructor
public class InventoryServiceImpl implements InventoryService{

    private final InventoryRepository inventoryRepository;
    private final HotelMinPriceRepository hotelMinPriceRepository;
    private final RoomRepository roomRepository;
    private final ModelMapper modelMapper;

    @Override
    public void initializeRoomForYear(Room room) {

        LocalDate today = LocalDate.now();
        LocalDate endDate = today.plusYears(1);

        for(; !today.isAfter(endDate); today = today.plusDays(1)){

            Inventory inventory = Inventory.builder().
                    hotel(room.getHotel()).
                    room(room).
                    bookedCount(0).
                    reservedCount(0).
                    city(room.getHotel().getCity()).
                    date(today).
                    closed(false).
                    price(room.getBasePrice()).
                    surgeFactor(BigDecimal.ONE).
                    totalCount(room.getTotalCount())
                    .build();
            inventoryRepository.save(inventory);
        }
        log.info("Created inventory for Room with id : {}", room.getId());
    }

    @Override
    public void deleteByRoom(Room room) {
        inventoryRepository.deleteByRoom(room);
        log.info("Deleted all inventory for room with id : {}", room.getId());
    }

    @Override
    public Page<HotelPriceDto> searchHotels(HotelSearchRequest hotelSearchRequest) {
        log.info("searching hotels for {} city from {} to {}",hotelSearchRequest.getCity(), hotelSearchRequest.getStartDate(), hotelSearchRequest.getEndDate());

        Pageable pageable = PageRequest.of(hotelSearchRequest.getPage(), hotelSearchRequest.getSize());
        long dateCount = ChronoUnit.DAYS.between(hotelSearchRequest.getStartDate(), hotelSearchRequest.getEndDate()) + 1;

        Page<HotelPriceDto> hotelPage =
        hotelMinPriceRepository.findHotelsWithAvailableInventory(hotelSearchRequest.getCity(),
                hotelSearchRequest.getStartDate(),
                hotelSearchRequest.getEndDate(),
                hotelSearchRequest.getRoomsCount(),
                dateCount,
                pageable
                );

        return hotelPage;

    }

    @Override
    public List<InventoryDto> getAllInventoryByRoom(Long roomId) {
        Room room = roomRepository.findById(roomId).orElseThrow( () ->
                new ResourceNotFoundException("Room does not exist with id -" + roomId)
        );

        User user = getCurrentUser();
        if(user.equals(room.getHotel().getOwner())){
            throw new AccessDeniedException("You are not the owner of the hotel with id "+ room.getHotel().getId());
        }

        log.info("Getting all inventories for room with id -" + roomId);
         List<Inventory> inventoryList =  inventoryRepository.findByRoomOrderByDate(room);

        return inventoryList.stream()
                .map(inventory -> modelMapper.map(inventory, InventoryDto.class))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void updateInventory(Long roomId, UpdateInventoryRequestDto requestDto){

        Room room = roomRepository.findById(roomId).orElseThrow( () ->
                new ResourceNotFoundException("Room does not exist with id -" + roomId)
        );

        User user = getCurrentUser();
        if(user.equals(room.getHotel().getOwner())){
            throw new AccessDeniedException("You are not the owner of the hotel with id "+ roomId);
        }

        log.info("Updating all inventories of room with id : {} between start date : {} and end date : {}"
                  ,roomId,requestDto.getStartDate(), requestDto.getEndDate());

        inventoryRepository.getInventoryAndLockBeforeUpdate(
                roomId,
                requestDto.getStartDate(),
                requestDto.getEndDate()
        );

        inventoryRepository.updateInventory(
                roomId,
                requestDto.getStartDate(),
                requestDto.getEndDate(),
                requestDto.getSurgeFactor(),
                requestDto.getClosed()
        );
    }
}
