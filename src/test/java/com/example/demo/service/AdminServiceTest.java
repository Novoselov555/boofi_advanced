package com.example.demo.service;

import com.example.demo.dto.UserDto;
import com.example.demo.entity.Role;
import com.example.demo.entity.User;
import com.example.demo.entity.Booking;
import com.example.demo.repository.UserRepository;
import com.example.demo.repository.BookingRepository;
import com.example.demo.exception.auth.UserNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class AdminServiceTest {
    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private AdminService adminService;

    @Test
    void testFindById_UserExists() {
        User user = new User();
        user.setId(1L);
        user.setName("efim");
        user.setEmail("efim@mail.ru");
        user.setPassword("encoded123");
        user.setAuthenticated(true);
        user.setRole(Role.USER);

        Mockito.when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        User found = adminService.findById(1L);
        assertEquals(user, found);
    }

    @Test
    void testFindById_UserNotFound() {
        Mockito.when(userRepository.findById(2L)).thenReturn(Optional.empty());
        assertThrows(UserNotFoundException.class, () -> adminService.findById(2L));
    }

    @Test
    void testGetUsers() {
        User user1 = new User();
        user1.setId(1L);
        user1.setName("user1");
        user1.setEmail("user1@mail.ru");
        user1.setPassword("p1");
        user1.setAuthenticated(true);
        user1.setRole(Role.USER);

        User user2 = new User();
        user2.setId(2L);
        user2.setName("user2");
        user2.setEmail("user2@mail.ru");
        user2.setPassword("p2");
        user2.setAuthenticated(true);
        user2.setRole(Role.USER);

        List<User> users = List.of(user1, user2);
        Mockito.when(userRepository.findUsersWithRoleUser()).thenReturn(users);

        List<User> result = adminService.getUsers();
        assertEquals(2, result.size());
        assertEquals("user1", result.get(0).getName());
        assertEquals("user2", result.get(1).getName());
    }

    @Test
    void testUpdateById_UserExists() {
        User user = new User();
        user.setId(1L);
        user.setName("old");
        user.setEmail("old@mail.ru");
        user.setPassword("pass");
        user.setAuthenticated(true);
        user.setRole(Role.USER);

        UserDto userDto = new UserDto();
        userDto.setName("new");
        userDto.setEmail("new@mail.ru");
        userDto.setRole(Role.ADMIN);

        Mockito.when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        Mockito.when(userRepository.save(Mockito.any(User.class))).thenAnswer(inv -> inv.getArgument(0));
        User updated = adminService.updateById(1L, userDto);

        assertEquals("new", updated.getName());
        assertEquals("new@mail.ru", updated.getEmail());
        assertEquals(Role.ADMIN, updated.getRole());
    }

    @Test
    void testUpdateById_UserNotFound() {
        UserDto userDto = new UserDto();
        Mockito.when(userRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(UserNotFoundException.class, () -> adminService.updateById(99L, userDto));
    }

    @Test
    void testDeleteById_UserExists() {
        Mockito.when(userRepository.existsById(1L)).thenReturn(true);
        adminService.deleteById(1L);
        Mockito.verify(userRepository).deleteById(1L);
    }

    @Test
    void testDeleteById_UserNotFound() {
        Mockito.when(userRepository.existsById(2L)).thenReturn(false);
        assertThrows(UserNotFoundException.class, () -> adminService.deleteById(2L));
    }

    @Test
    void testDeleteAll() {
        adminService.deleteAllUsers();
        Mockito.verify(userRepository).deleteAllUsers();
    }
}
