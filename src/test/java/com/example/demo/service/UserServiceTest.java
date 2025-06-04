package com.example.demo.service;

import com.example.demo.entity.Role;
import com.example.demo.entity.User;
import com.example.demo.entity.Booking;
import com.example.demo.repository.BookingRepository;
import com.example.demo.exception.auth.UserNotFoundException;
import com.example.demo.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {
    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private BookingRepository bookingRepository;
    @InjectMocks
    private UserService userService;

    @Test
    void testFindById_UserExists() {
        User user = new User();
        user.setId(1L);
        user.setName("efim");
        user.setEmail("efim@mail.ru");
        user.setPassword("encoded123");
        user.setAuthenticated(true);
        user.setRole(Role.USER);
        user.setBookings(new ArrayList<>());
        Mockito.when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        User found = userService.findById(1L);
        assertEquals(user, found);
    }

    @Test
    void testFindById_UserNotFound() {
        Mockito.when(userRepository.findById(2L)).thenReturn(Optional.empty());
        assertThrows(UserNotFoundException.class, () -> userService.findById(2L));
    }

    @Test
    void testUpdate() {
        User original = new User();
        original.setId(1L);
        original.setName("efim");
        original.setEmail("efim@mail.ru");
        original.setPassword("encoded123");
        original.setAuthenticated(true);
        original.setRole(Role.USER);
        original.setBookings(new ArrayList<>());

        User updatedData = new User();
        updatedData.setName("egor");
        updatedData.setEmail("egor@mail.ru");
        updatedData.setPassword("qwe");

        Mockito.when(userRepository.findById(1L)).thenReturn(Optional.of(original));
        Mockito.when(passwordEncoder.encode("qwe")).thenReturn("encodedQwe");
        Mockito.when(userRepository.save(Mockito.any(User.class))).thenAnswer(i -> i.getArgument(0));

        User updated = userService.update(1L, updatedData);
        assertEquals("egor", updated.getName());
        assertEquals("egor@mail.ru", updated.getEmail());
        assertEquals("encodedQwe", updated.getPassword());
    }

    @Test
    void testDelete_UserExists() {
        Mockito.when(userRepository.existsById(1L)).thenReturn(true);
        userService.delete(1L);
        Mockito.verify(userRepository).deleteById(1L);
    }

    @Test
    void testDelete_UserNotFound() {
        Mockito.when(userRepository.existsById(2L)).thenReturn(false);
        assertThrows(UserNotFoundException.class, () -> userService.delete(2L));
    }

    @Test
    void testGetBookingsByUserId() {
        Long userId = 7L;
        List<Booking> bookings = List.of(
                new Booking(),
                new Booking()
        );
        Mockito.when(bookingRepository.getAllBookingsByPlaceId(userId)).thenReturn(bookings);

        List<Booking> result = userService.getBookingsByUserId(userId);

        assertEquals(2, result.size());
        assertSame(bookings, result);
    }
}
