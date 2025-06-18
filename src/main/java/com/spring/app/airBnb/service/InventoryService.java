package com.spring.app.airBnb.service;

import com.spring.app.airBnb.entity.Room;

public interface InventoryService {

    void initializeRoomForYear(Room room);

    void deleteFutureInventory (Room room);
}
