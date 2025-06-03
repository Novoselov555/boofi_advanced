package com.example.demo.controller;

import com.example.demo.dto.BookingDto;
import com.example.demo.entity.Booking;
import com.example.demo.entity.User;
import com.example.demo.security.ValidateAuthHeader;
import com.example.demo.security.ValidateBooking;
import com.example.demo.service.BookingService;
import com.example.demo.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Tag(name = "User", description = "User API")
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final ValidateAuthHeader validateAuthHeader;
    private final ValidateBooking validateBooking;
    private final BookingService bookingService;


    // Ручки профиля пользователя
    @Operation(summary = "Получение личных данных")
    @GetMapping("/profile/me")
    public ResponseEntity<?> getMe(@RequestHeader("Authorization") String authHeader) {
        Long id = validateAuthHeader.isValid(authHeader);
        User user = userService.findById(id);
        return ResponseEntity
                .ok()
                .body(user);
    }

    @Operation(summary = "Пользователь может изменить свои личные данные")
    @PostMapping("/profile/me")
    public ResponseEntity<?> update(@RequestHeader("Authorization") String authHeader, @RequestBody User userBody) {
        Long id = validateAuthHeader.isValid(authHeader);
        User user = userService.update(id, userBody);
        return ResponseEntity
                .ok()
                .body(user);
    }

    @Operation(summary = "Ливает с деревни, тима раков")
    @PatchMapping("/profile/me")
    public ResponseEntity<?> logout(@RequestHeader("Authorization") String authHeader) {
        Long id = validateAuthHeader.isValid(authHeader);
        User user = userService.logout(id);
        return ResponseEntity
                .ok()
                .body(user);
    }

    @Operation(summary = "Удаление аккаунта")
    @DeleteMapping("/profile/me")
    public ResponseEntity<?> delete(@RequestHeader("Authorization") String authHeader) {
        Long id = validateAuthHeader.isValid(authHeader);
        userService.delete(id);
        return ResponseEntity.ok().build();
    }


    // Ручки бронирований
    @Operation(summary = "Получение всех бронирований пользователя")
    @GetMapping("/bookings")
    public ResponseEntity<?> getUserBookings(@RequestHeader("Authorization") String authHeader) {
        Long userId = validateAuthHeader.isValid(authHeader);
        List<Booking> bookings = userService.getBookingsByUserId(userId);
        return ResponseEntity
                .ok()
                .body(bookings);
    }

    @Operation(summary = "Меняем время бронирования")
    @PatchMapping("/bookings/{bookingId}")
    public ResponseEntity<?> changeTime(@RequestHeader("Authorization") String authHeader, @PathVariable Long bookingId, @RequestBody BookingDto bookingDto) {
        Long userId = validateAuthHeader.isValid(authHeader);
        validateBooking.hasUserBooking(userId, bookingId);
        Booking booking = bookingService.changeTime(userId, bookingId, bookingDto);
        return ResponseEntity
                .ok()
                .body(booking);
    }

    @Operation(summary = "Отменяем бронирование")
    @PatchMapping("/bookings/cancel/{bookingId}")
    public ResponseEntity<?> cancelBooking(@RequestHeader("Authorization") String authHeader, @PathVariable Long bookingId) {
        Long userId = validateAuthHeader.isValid(authHeader);
        validateBooking.hasUserBooking(userId, bookingId);
        Booking booking = bookingService.cancelBooking(bookingId);
        return ResponseEntity
                .ok()
                .body(booking);
    }
}
