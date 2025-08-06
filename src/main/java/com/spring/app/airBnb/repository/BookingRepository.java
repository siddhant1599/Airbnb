package com.spring.app.airBnb.repository;

import com.spring.app.airBnb.entity.Booking;
import com.spring.app.airBnb.entity.Hotel;
import com.spring.app.airBnb.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface BookingRepository extends JpaRepository<Booking ,Long>{

    Optional<Booking> findByPaymentSessionId(String sessionId);

    List<Booking> findByHotel(Hotel hotel);

    List<Booking> findByHotelAndCreatedAtBetween(Hotel hotel, LocalDateTime startDate, LocalDateTime endDate);

    List<Booking> findAllByUser(User user);
}
