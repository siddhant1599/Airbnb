package com.spring.app.airBnb.controller;

import com.spring.app.airBnb.dto.HotelDto;
import com.spring.app.airBnb.dto.HotelInfoDto;
import com.spring.app.airBnb.dto.HotelSearchRequest;
import com.spring.app.airBnb.service.HotelService;
import com.spring.app.airBnb.service.InventoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/hotels")
@RequiredArgsConstructor
public class HotelBrowseController {


    private final InventoryService inventoryService;
    private final HotelService hotelService;

    @GetMapping("/search")
    public ResponseEntity<Page<HotelDto>> searchHotel(@RequestBody HotelSearchRequest hotelSearchRequest){
        Page<HotelDto> page =  inventoryService.searchHotels(hotelSearchRequest);
        return ResponseEntity.ok(page);
    }


    @GetMapping("/{hotelId}/info")
    public ResponseEntity<HotelInfoDto> getHotelInfo(@PathVariable Long hotelId){
        return ResponseEntity.ok(hotelService.getHotelInfoById(hotelId));
    }



}
