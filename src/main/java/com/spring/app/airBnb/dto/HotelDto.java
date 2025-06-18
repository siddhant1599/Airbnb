package com.spring.app.airBnb.dto;

import com.spring.app.airBnb.entity.HotelContactInfo;
import com.spring.app.airBnb.entity.Room;
import lombok.Data;
import java.util.List;

@Data
public class HotelDto {


    private Long id;
    private String name;
    private String city;
    private String[] photos;
    private String[] amenities;
    private HotelContactInfo hotelContactInfo;
    private List<Room> rooms;
    private Boolean active;

}
