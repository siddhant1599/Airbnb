package com.spring.app.airBnb.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProfileUpdateRequestDto {

    private String name;
    private LocalDate dateOfBirth;
    private String gender;
}
