package com.spring.app.airBnb.service;

import com.spring.app.airBnb.dto.ProfileUpdateRequestDto;
import com.spring.app.airBnb.dto.UserDto;
import com.spring.app.airBnb.entity.User;

public interface UserService {

    User getUserById(Long id);

    void updateProfile(ProfileUpdateRequestDto requestDto);

    UserDto getUserProfile();
}
