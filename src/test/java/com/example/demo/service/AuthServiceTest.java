package com.example.demo.service;

import com.example.demo.dto.LoginRequest;
import com.example.demo.dto.RegisterRequest;
import com.example.demo.entity.Role;
import com.example.demo.entity.User;
import com.example.demo.exception.auth.IncorrectCredentialsException;
import com.example.demo.exception.auth.UserAlreadyExistsException;
import com.example.demo.repository.UserRepository;
import com.example.demo.repository.CoworkingRepository;
import com.example.demo.repository.PlaceRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {
    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordEncoder passwordEncoder;


    @InjectMocks
    private AuthService authService;

    @Test
    public void testInitAdmin() {
        Mockito.when(userRepository.count()).thenReturn(0L);
        Mockito.when(passwordEncoder.encode("admin123")).thenReturn("encodedAdmin");
        authService.init();
        Mockito.verify(userRepository)
                .save(Mockito.argThat(user ->
                        "admin".equals(user.getName()) &&
                                "admin@boofi.com".equals(user.getEmail()) &&
                                "encodedAdmin".equals(user.getPassword()) &&
                                Role.ADMIN.equals(user.getRole()) &&
                                user.isAuthenticated()
                ));
    }

    @Test
    public void testRegister_UserExists() {
        RegisterRequest registerRequest = new RegisterRequest("efim", "efim@mail.ru", "123");
        Mockito.when(userRepository.existsByEmail("efim@mail.ru")).thenReturn(true);
        assertThrows(UserAlreadyExistsException.class,
                () -> authService.register(registerRequest));
    }

    @Test
    public void testLogin_UserNotFound() {
        LoginRequest loginRequest = new LoginRequest("efim@mail.ru", "123");
        Mockito.when(userRepository.findByEmail("efim@mail.ru")).thenReturn(Optional.empty());
        assertThrows(IncorrectCredentialsException.class,
                () -> authService.login(loginRequest));
    }

    @Test
    public void testLogin_IncorrectPassword() {
        LoginRequest loginRequest = new LoginRequest("efim@mail.ru", "123");

        User stored = new User();
        stored.setId(1L);
        stored.setName("efim");
        stored.setEmail("efim@mail.ru");
        stored.setPassword("encodedPw");
        stored.setAuthenticated(false);
        stored.setRole(Role.USER);

        Mockito.when(userRepository.findByEmail("efim@mail.ru")).thenReturn(Optional.of(stored));
        Mockito.when(passwordEncoder.matches("123", "encodedPw")).thenReturn(false);

        assertThrows(IncorrectCredentialsException.class,
                () -> authService.login(loginRequest)
        );
    }

    @Test
    public void testRegisterUser() {
        RegisterRequest registerRequest = new RegisterRequest("efim", "efim@mail.ru", "123");
        Mockito.when(userRepository.existsByEmail("efim@mail.ru")).thenReturn(false);
        Mockito.when(passwordEncoder.encode("123")).thenReturn("encoded123");

        HashMap<String, String> data = authService.register(registerRequest);

        Mockito.verify(userRepository).save(Mockito.argThat(user ->
                "efim".equals(user.getName()) &&
                        "efim@mail.ru".equals(user.getEmail()) &&
                        "encoded123".equals(user.getPassword()) &&
                        user.isAuthenticated() &&
                        Role.USER.equals(user.getRole())
        ));

        String expectedToken = Base64.getEncoder().encodeToString("efim@mail.ru:123".getBytes());
        assertEquals(expectedToken, data.get("token"));
        assertEquals(String.valueOf(Role.USER), data.get("role"));
    }

    @Test
    public void testLoginUser() {
        LoginRequest loginRequest = new LoginRequest("efim@mail.ru", "123");

        User stored = new User();
        stored.setId(42L);
        stored.setName("efim");
        stored.setEmail("efim@mail.ru");
        stored.setPassword("encoded123");
        stored.setAuthenticated(true);
        stored.setRole(Role.USER);

        Mockito.when(userRepository.findByEmail("efim@mail.ru")).thenReturn(Optional.of(stored));
        Mockito.when(passwordEncoder.matches("123", "encoded123")).thenReturn(true);

        HashMap<String, String> data = authService.login(loginRequest);

        Mockito.verify(userRepository).findByEmail("efim@mail.ru");
        Mockito.verify(passwordEncoder).matches("123", "encoded123");

        String expectedToken = Base64.getEncoder().encodeToString("efim@mail.ru:123".getBytes());
        assertEquals(expectedToken, data.get("token"));
        assertEquals(String.valueOf(Role.USER), data.get("role"));
        assertEquals("42", data.get("id"));
    }
}
