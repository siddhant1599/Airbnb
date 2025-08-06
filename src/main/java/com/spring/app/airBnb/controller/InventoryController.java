package com.spring.app.airBnb.controller;

import com.spring.app.airBnb.dto.InventoryDto;
import com.spring.app.airBnb.dto.UpdateInventoryRequestDto;
import com.spring.app.airBnb.service.InventoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/inventory")
@RequiredArgsConstructor
public class InventoryController {

    private final InventoryService inventoryService;

    @GetMapping("/rooms/{roomId}")
    public ResponseEntity<List<InventoryDto>> getAllInventoryByRoom(@PathVariable Long roomId){
        return ResponseEntity.ok(inventoryService.getAllInventoryByRoom(roomId));
    }

    @PatchMapping("/rooms/{roomsId}")
    public ResponseEntity<Void> updateInventory(@PathVariable Long roomsId, @RequestBody UpdateInventoryRequestDto requestDto) {

        inventoryService.updateInventory(roomsId,requestDto);
        return ResponseEntity.noContent().build();
    }
}
