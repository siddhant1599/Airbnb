package com.spring.app.airBnb.repository;

import com.spring.app.airBnb.entity.Hotel;
import com.spring.app.airBnb.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HotelRepository extends JpaRepository<Hotel, Long> {
    List<Hotel> findByOwner(User user);
}
