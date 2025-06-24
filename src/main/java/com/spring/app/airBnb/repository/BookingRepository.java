package com.spring.app.airBnb.repository;

import com.spring.app.airBnb.entity.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BookingRepository extends JpaRepository<Booking ,Long>{

}
