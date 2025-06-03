package com.example.demo.service;

import com.example.demo.dto.BookingDto;
import com.example.demo.entity.Booking;
import com.example.demo.entity.BookingStatus;
import com.example.demo.entity.User;
import com.example.demo.exception.auth.UserNotFoundException;
import com.example.demo.exception.booking.BookingNotFoundException;
import com.example.demo.repository.BookingRepository;
import com.example.demo.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
@AllArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final BookingRepository bookingRepository;

    public User findById(Long id) {
        return userRepository.findById(id).orElseThrow(() -> new UserNotFoundException("Пользователь не найден: " + id));
    }

    public User update(Long id, User user) {
        User currentUser = findById(id);
        currentUser.setName(user.getName());
        currentUser.setEmail(user.getEmail());
        currentUser.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(currentUser);
    }

    public User logout(Long id) {
        User user = findById(id);
        user.setAuthenticated(false);
        return userRepository.save(user);
    }

    public void delete(Long id) {
        if (!userRepository.existsById(id)) {
            throw new UserNotFoundException("Пользователь не найден: " + id);
        }
        userRepository.deleteById(id);
    }

    // Получение всех бронирований по userId
    public List<Booking> getBookingsByUserId(Long userId) {
        return bookingRepository.getAllBookingsByPlaceId(userId);
    }
}
