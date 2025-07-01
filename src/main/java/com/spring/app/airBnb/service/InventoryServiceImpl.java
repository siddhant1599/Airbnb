package com.spring.app.airBnb.service;

import com.spring.app.airBnb.dto.HotelDto;
import com.spring.app.airBnb.dto.HotelPriceDto;
import com.spring.app.airBnb.dto.HotelSearchRequest;
import com.spring.app.airBnb.entity.Inventory;
import com.spring.app.airBnb.entity.Room;
import com.spring.app.airBnb.repository.HotelMinPriceRepository;
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

    private final InventoryRepository inventoryRepository;
    private final HotelMinPriceRepository hotelMinPriceRepository;
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
}
