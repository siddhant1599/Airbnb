package com.spring.app.airBnb.service;

import com.spring.app.airBnb.dto.ProfileUpdateRequestDto;
import com.spring.app.airBnb.dto.UserDto;
import com.spring.app.airBnb.entity.User;
import com.spring.app.airBnb.entity.enums.Gender;
import com.spring.app.airBnb.exception.ResourceNotFoundException;
import com.spring.app.airBnb.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import static com.spring.app.airBnb.util.AppUtils.getCurrentUser;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService, UserDetailsService {

    private final UserRepository userRepository;
    private final ModelMapper mapper;

    @Override
    public User getUserById(Long id) {
       return userRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("User not found with id - " + id));
    }

    @Override
    public void updateProfile(ProfileUpdateRequestDto requestDto) {
        User user = getCurrentUser();

        if(requestDto.getName()!= null)user.setName(requestDto.getName());
        if(requestDto.getGender()!= null)user.setGender(Gender.valueOf(requestDto.getGender()));
        if(requestDto.getDateOfBirth()!= null)user.setDateOfBirth(requestDto.getDateOfBirth());

        userRepository.save(user);
    }

    @Override
    public UserDto getUserProfile() {
        User user = getCurrentUser();

        return mapper.map(user, UserDto.class);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByEmail(username).orElseThrow(() -> new ResourceNotFoundException("User not found with email -" + username));
    }
}
