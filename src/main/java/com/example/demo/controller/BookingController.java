package com.example.demo.controller;

import com.example.demo.dto.BookingDto;
import com.example.demo.entity.Booking;
import com.example.demo.security.ValidateAuthHeader;
import com.example.demo.service.BookingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Tag(name = "Booking", description = "Booking API")
@RequestMapping("/bookings")
@RequiredArgsConstructor
public class BookingController {
    private final ValidateAuthHeader validateAuthHeader;
    private final BookingService bookingService;

    @Operation(summary = "Получение всех бронирований")
    @GetMapping
    public ResponseEntity<?> getAllBookings(@RequestHeader("Authorization") String authHeader) {
        validateAuthHeader.isValid(authHeader);
        List<Booking> bookings = bookingService.getAllBookings();
        return ResponseEntity
                .ok()
                .body(bookings);
    }

    @Operation(summary = "Получение всех бронирований по placeId")
    @GetMapping("/{placeId}")
    public ResponseEntity<?> getBookingsByPlaceId(@RequestHeader("Authorization") String authHeader, @PathVariable Long placeId) {
        validateAuthHeader.isValid(authHeader);
        List<Booking> bookings = bookingService.getBookingsByPlaceId(placeId);
        return ResponseEntity
                .ok()
                .body(bookings);
    }

    @Operation(summary = "Бронирование места по placeId")
    @PostMapping("/{placeId}")
    public ResponseEntity<?> bookAPlace(@RequestHeader("Authorization") String authHeader, @PathVariable Long placeId, @RequestBody BookingDto bookingDto) {
        Long userId = validateAuthHeader.isValid(authHeader);
        Booking booking = bookingService.bookAPlace(userId, placeId, bookingDto);
        return ResponseEntity
                .ok()
                .body(booking);
    }
}
