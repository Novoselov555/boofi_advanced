package com.example.demo.service;

import com.example.demo.dto.UserDto;
import com.example.demo.entity.Booking;
import com.example.demo.entity.BookingStatus;
import com.example.demo.entity.User;

import com.example.demo.exception.auth.UserNotFoundException;
import com.example.demo.repository.BookingRepository;
import com.example.demo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminService {
    private final UserRepository userRepository;
    private final BookingService bookingService;
    private final BookingRepository bookingRepository;

    public User findById(Long id) {
        return userRepository.findById(id).orElseThrow(() -> new UserNotFoundException("Пользователь не найден: " + id));
    }

    public List<User> getUsers() {
        return userRepository.findUsersWithRoleUser();
    }

    public User updateById(Long id, UserDto userDto) {
        User user = findById(id);
        user.setName(userDto.getName());
        user.setEmail(userDto.getEmail());
        user.setRole(userDto.getRole());
        return userRepository.save(user);
    }

    public void deleteById(Long id) {
        if (!userRepository.existsById(id)) {
            throw new UserNotFoundException("Пользователь не найден: " + id);
        }
        userRepository.deleteById(id);
    }

    public void deleteAllUsers() {
        userRepository.deleteAllUsers();
    }

    public Booking rejectBookingByBookingId(Long bookingId) {
        Booking booking = bookingService.findBookingByBookingId(bookingId);
        booking.setStatus(BookingStatus.REJECTED);
        return bookingRepository.save(booking);
    }
}
