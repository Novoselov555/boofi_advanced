package com.example.demo.service;

import com.example.demo.dto.UserDto;
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
        User user = new User(1L, "efim", "efim@mail.ru", "encoded123", true, Role.USER, new ArrayList<>(), new ArrayList<>());
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
        List<User> users = List.of(
                new User(1L, "user1", "user1@mail.ru", "p1", true, Role.USER, new ArrayList<>(), new ArrayList<>()),
                new User(2L, "user2", "user2@mail.ru", "p2", true, Role.USER, new ArrayList<>(), new ArrayList<>())
        );
        Mockito.when(userRepository.findUsersWithRoleUser()).thenReturn(users);
        List<User> result = adminService.getUsers();
        assertEquals(2, result.size());
        assertEquals("user1", result.get(0).getName());
        assertEquals("user2", result.get(1).getName());
    }

    @Test
    void testUpdateById_UserExists() {
        User user = new User(1L, "old", "old@mail.ru", "pass", true, Role.USER, new ArrayList<>(), new ArrayList<>());
        UserDto userDto = new UserDto();
        userDto.setName("new");
        userDto.setEmail("new@mail.ru");
        userDto.setRole(Role.ADMIN);

        Mockito.when(userRepository.findById(1L)).thenReturn(Optional.of(user));
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
