package com.example.demo.controller;

import com.example.demo.dto.LoginRequest;
import com.example.demo.dto.RegisterRequest;
import com.example.demo.entity.Role;
import com.example.demo.entity.User;
import com.example.demo.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.junit.jupiter.Testcontainers;


import java.util.Base64;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@Testcontainers
public class AuthControllerIntegrationTest extends DataBaseConnect {
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private TestRestTemplate testRestTemplate;

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    public void cleanUp() {
        userRepository.deleteAll();
    }

    @Test
    public void testRegisterUser() {
        RegisterRequest registerRequest = new RegisterRequest("efim", "efim@mail.ru", "123");
        ResponseEntity<Map> response = testRestTemplate.postForEntity("/auth/register", registerRequest, Map.class);

        assertEquals(HttpStatus.OK, response.getStatusCode(), "Статусы отличаются");

        String credentials = (String) response.getBody().get("credentials");

        String decodedCredentials = new String(Base64.getDecoder().decode(credentials));
        String decodedEmail = decodedCredentials.split(":")[0];
        String decodedPassword = decodedCredentials.split(":")[1];

        assertEquals("efim@mail.ru", decodedEmail, "Почты не совпадают");
        assertTrue(passwordEncoder.matches("123", decodedPassword), "Пароли не совпадают");
    }

    @Test
    public void testRegisterAlreadyExistingUser() {
        User user = new User(null,"efim", "efim@mail.ru", "123", Role.USER);
        userRepository.save(user);

        RegisterRequest registerRequest = new RegisterRequest("efim", "efim@mail.ru", "123");
        ResponseEntity<Map> response = testRestTemplate.postForEntity("/auth/register", registerRequest, Map.class);

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode(), "Статусы не совпадают");
    }

    @Test
    public void testLoginUser() {
        User user = new User(null,"efim", "efim@mail.ru", passwordEncoder.encode("123"), Role.USER);
        userRepository.save(user);

        LoginRequest loginRequest = new LoginRequest("efim@mail.ru", "123");
        ResponseEntity<Map> response = testRestTemplate.postForEntity("/auth/login", loginRequest, Map.class);

        assertEquals(HttpStatus.OK, response.getStatusCode(), "Статусы не совпадают");

        String credentials = (String) response.getBody().get("credentials");

        String decodedCredentials = new String(Base64.getDecoder().decode(credentials));
        String decodedEmail = decodedCredentials.split(":")[0];
        String decodedPassword = decodedCredentials.split(":")[1];

        assertEquals("efim@mail.ru", decodedEmail, "Почты не совпадают");
        assertTrue(passwordEncoder.matches("123", decodedPassword), "Пароли не совпадают");
    }

    @Test
    public void testLoginIncorrectPassword() {
        User user = new User(null,"efim", "efim@mail.ru", passwordEncoder.encode("1234"), Role.USER);
        userRepository.save(user);

        LoginRequest loginRequest = new LoginRequest("efim@mail.ru", "123");
        ResponseEntity<Map> response = testRestTemplate.postForEntity("/auth/login", loginRequest, Map.class);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode(), "Статусы не совпадают");
    }

    @Test
    public void testLoginNonExistingUser() {
        LoginRequest loginRequest = new LoginRequest("efim@mail.ru", "123");
        ResponseEntity<Map> response = testRestTemplate.postForEntity("/auth/login", loginRequest, Map.class);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode(), "Статусы не совпадают");
    }
}
