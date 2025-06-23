package com.spring.app.airBnb.service;

import com.spring.app.airBnb.dto.HotelDto;
import com.spring.app.airBnb.dto.HotelSearchRequest;
import com.spring.app.airBnb.entity.Hotel;
import com.spring.app.airBnb.entity.Inventory;
import com.spring.app.airBnb.entity.Room;
import com.spring.app.airBnb.repository.InventoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

@Service
@Slf4j
@RequiredArgsConstructor
public class InventoryServiceImpl implements InventoryService{

    private final InventoryRepository repository;
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
                    city(room.getHotel().getCity()).
                    date(today).
                    closed(false).
                    price(room.getBasePrice()).
                    surgeFactor(BigDecimal.ONE).
                    totalCount(room.getTotalCount())
                    .build();
            repository.save(inventory);
        }
        log.info("Created inventory for Room with id : {}", room.getId());
    }

    @Override
    public void deleteByRoom(Room room) {
        repository.deleteByRoom(room);
        log.info("Deleted  inventory for room with id : {} from existing date : ", room.getId());
    }

    @Override
    public Page<HotelDto> searchHotels(HotelSearchRequest hotelSearchRequest) {
        Pageable pageable = PageRequest.of(hotelSearchRequest.getPage(), hotelSearchRequest.getSize());
        long dateCount = ChronoUnit.DAYS.between(hotelSearchRequest.getStartDate(), hotelSearchRequest.getEndDate()) + 1;

        Page<Hotel> hotelPage =
        repository.findHotelsWithAvailableInventory(hotelSearchRequest.getCity(),
                hotelSearchRequest.getStartDate(),
                hotelSearchRequest.getEndDate(),
                hotelSearchRequest.getRoomsCount(),
                dateCount,
                pageable
                );

        return hotelPage.map(hotel -> modelMapper.map(hotel, HotelDto.class));

    }
}
