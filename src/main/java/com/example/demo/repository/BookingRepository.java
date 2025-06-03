package com.example.demo.repository;

import com.example.demo.entity.Booking;
import com.example.demo.entity.Place;
import com.example.demo.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    @Query("SELECT b FROM Booking b WHERE b.place.id = :placeId ")
    List<Booking> getAllBookingsByPlaceId(@Param("placeId") Long placeId);

    @Query("SELECT b FROM Booking b WHERE b.user.id = :userID")
    List<Booking> getBookingsByUserId(@Param("userId") Long userId);
}
