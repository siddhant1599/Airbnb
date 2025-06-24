package com.spring.app.airBnb.controller;

import com.spring.app.airBnb.dto.HotelDto;
import com.spring.app.airBnb.service.HotelService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/hotels")
@RequiredArgsConstructor
@Slf4j
public class HotelController {

    private final HotelService hotelService;


    @PostMapping()
    public ResponseEntity<HotelDto> createNewHotel(@RequestBody HotelDto hotelDto){
        HotelDto hotelDto1 =  hotelService.createNewHotel(hotelDto);
        return new ResponseEntity<>(hotelDto1, HttpStatus.CREATED);
    }

    @GetMapping("/{hotelId}")
    public ResponseEntity<HotelDto> getHotelId(@PathVariable Long hotelId){
        HotelDto hotelDto = hotelService.getHotelById((hotelId));
        return ResponseEntity.ok(hotelDto);
    }

    @GetMapping()
    public ResponseEntity<List<HotelDto>> getAllHotels(){
        List<HotelDto> hotelDtoList = hotelService.getAllHotels();
        return ResponseEntity.ok(hotelDtoList);
    }

    @PutMapping("/{hotelId}")
    public ResponseEntity<HotelDto> updateHotelById (@PathVariable Long hotelId, @RequestBody HotelDto hotelDto){
        HotelDto hotelDto1 = hotelService.updateHotelById(hotelDto,hotelId);

        return new ResponseEntity<>(hotelDto1, HttpStatus.OK);
    }

    @DeleteMapping("/{hotelId}")
    public ResponseEntity<Void> deleteHotelById(@PathVariable Long hotelId){

        hotelService.deleteHotelById((hotelId));

        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{hotelId}/activate")
    public ResponseEntity<HotelDto> activateHotelStatus(@PathVariable Long hotelId){

        HotelDto hotelDto=  hotelService.activateHotelStatus(hotelId);

        return new ResponseEntity<>(hotelDto, HttpStatus.OK);
    }

}
