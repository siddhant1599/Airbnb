package com.spring.app.airBnb.dto;

import com.spring.app.airBnb.entity.Booking;
import com.spring.app.airBnb.entity.User;
import com.spring.app.airBnb.entity.enums.Gender;
import jakarta.persistence.*;
import lombok.Data;

import java.util.Set;

@Data
public class GuestDto {


    private Long id;
    private User user;
    private String name;
    private Gender gender;
    private Integer age;

}
