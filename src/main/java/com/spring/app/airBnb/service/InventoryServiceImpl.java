package com.spring.app.airBnb.service;

import com.spring.app.airBnb.entity.Inventory;
import com.spring.app.airBnb.entity.Room;
import com.spring.app.airBnb.repository.InventoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;

@Service
@Slf4j
@RequiredArgsConstructor
public class InventoryServiceImpl implements InventoryService{

    private final InventoryRepository repository;

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
    public void deleteFutureInventory(Room room) {
        repository.deleteByDateAfterAndRoom(LocalDate.now(), room);
        log.info("Deleted future inventory for room with id : {} from existing date : {}", room.getId(), LocalDate.now());
    }
}
