package com.example.demo.service;

import com.example.demo.entity.Role;
import com.example.demo.entity.User;
import com.example.demo.exception.UserNotFoundException;
import com.example.demo.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.ArrayList;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {
    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @InjectMocks
    private UserService userService;

    @Test
    void testFindById_UserExists() {
        User user = new User(1L, "efim", "efim@mail.ru", "encoded123", true, Role.USER, new ArrayList<>(), new ArrayList<>());
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
        User original = new User(1L, "efim", "efim@mail.ru", "encoded123", true, Role.USER, new ArrayList<>(), new ArrayList<>());
        User updatedData = new User(null, "egor", "egor@mail.ru", "qwe", true, Role.USER, new ArrayList<>(), new ArrayList<>());

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
}
