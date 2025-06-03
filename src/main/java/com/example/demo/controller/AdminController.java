package com.example.demo.controller;

import com.example.demo.dto.BookingDto;
import com.example.demo.dto.UserDto;
import com.example.demo.entity.Booking;
import com.example.demo.entity.User;
import com.example.demo.repository.BookingRepository;
import com.example.demo.security.ValidateAuthHeader;
import com.example.demo.security.ValidateBooking;
import com.example.demo.service.AdminService;
import com.example.demo.service.BookingService;
import com.example.demo.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Tag(name = "Admin", description = "Admin API")
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {
    private final AdminService adminService;
    private final ValidateAuthHeader validateAuthHeader;
    private final BookingService bookingService;
    private final BookingRepository bookingRepository;
    private final ValidateBooking validateBooking;
    private final UserService userService;

    @Operation(summary = "Получение всех пользователей")
    @GetMapping("/users")
    public ResponseEntity<?> getUsers(@RequestHeader("Authorization") String authHeader) {
        validateAuthHeader.isAdminValid(authHeader);
        return ResponseEntity
                .ok()
                .body(adminService.getUsers());
    }

    @Operation(summary = "Обновление данных пользователя по userId")
    @PostMapping("/users/{id}")
    public ResponseEntity<?> updateById(@RequestHeader("Authorization") String authHeader, @PathVariable Long id, @RequestBody UserDto userDto) {
        validateAuthHeader.isAdminValid(authHeader);
        User recievedUser = adminService.updateById(id, userDto);
        return ResponseEntity
                .ok()
                .body(recievedUser);
    }

    @Operation(summary = "Удаление пользователя по userId")
    @DeleteMapping("users/{id}")
    public ResponseEntity<?> deleteById(@RequestHeader("Authorization") String authHeader, @PathVariable Long id) {
        validateAuthHeader.isAdminValid(authHeader);
        adminService.deleteById(id);
        return ResponseEntity
                .ok()
                .build();
    }

    @Operation(summary = "Удаление всех пользователей")
    @DeleteMapping("users")
    public ResponseEntity<?> deleteAll(@RequestHeader("Authorization") String authHeader) {
        validateAuthHeader.isAdminValid(authHeader);
        adminService.deleteAllUsers();
        return ResponseEntity
                .ok()
                .build();
    }

    @Operation(summary = "Получение всех бронирований")
    @GetMapping("bookings")
    public ResponseEntity<?> getAllBookings(@RequestHeader("Authorization") String authHeader) {
        validateAuthHeader.isAdminValid(authHeader);
        List<Booking> bookings = bookingService.getAllBookings();
        return ResponseEntity
                .ok()
                .body(bookings);
    }

    @Operation(summary = "Получение бронирований пользователя по userId")
    @GetMapping("bookings/{userId}")
    public ResponseEntity<?> getBookingsByUserId(@RequestHeader("Authorization") String authHeader, @PathVariable Long userId) {
        validateAuthHeader.isAdminValid(authHeader);
        List<Booking> bookings = bookingRepository.getBookingsByUserId(userId);
        return ResponseEntity
                .ok()
                .body(bookings);
    }

    @Operation(summary = "Отклонить бронирование по bookingId")
    @PatchMapping("bookings/cancel/{bookingId}")
    public ResponseEntity<?> cancelBookingsByUserId(@RequestHeader("Authorization") String authHeader, @PathVariable Long bookingId){
        validateAuthHeader.isAdminValid(authHeader);
        Booking booking = adminService.rejectBookingByBookingId(bookingId);
        return ResponseEntity
                .ok()
                .body(booking);
    }

    @Operation(summary = "Перенос бронирования по bookingId")
    @PatchMapping("booking/{bookingId}")
    public ResponseEntity<?> changeTime(@RequestHeader("Authorization") String authHeader, @PathVariable Long bookingId, @RequestBody BookingDto bookingDto){
        validateAuthHeader.isAdminValid(authHeader);
        Long userId = validateAuthHeader.isValid(authHeader);
        Booking booking = bookingService.changeTime(userId, bookingId, bookingDto);
        return ResponseEntity
                .ok()
                .body(booking);
    }
}
