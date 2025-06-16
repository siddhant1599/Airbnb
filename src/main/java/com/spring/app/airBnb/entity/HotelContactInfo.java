package com.spring.app.airBnb.entity;

import jakarta.persistence.Embeddable;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Embeddable
public class HotelContactInfo {

    private String address;

    private String phoneNumber;

    private String email;

    private String location;
}
