package com.example.demo.controller;

import com.example.demo.entity.Role;
import com.example.demo.entity.User;
import com.example.demo.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.ArrayList;
import java.util.Base64;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@Testcontainers
public class UserControllerIntegrationTest extends DataBaseConnect {
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

    private String buildAuthHeader(String email, String password) {
        String creds = email + ":" + password;
        return "Basic " + Base64.getEncoder().encodeToString(creds.getBytes());
    }

    @Test
    public void testUpdateUser_Success() {
        User user = new User();
        user.setName("egor");
        user.setEmail("egor@mail.ru");
        user.setPassword(passwordEncoder.encode("123"));
        user.setAuthenticated(true);
        user.setRole(Role.USER);
        user.setBookings(new ArrayList<>());
        user = userRepository.save(user);

        User updateBody = new User();
        updateBody.setName("EgorUpdated");
        updateBody.setEmail("egor@mail.ru");
        updateBody.setPassword("newpass");
        updateBody.setAuthenticated(true);
        updateBody.setRole(Role.USER);
        updateBody.setBookings(new ArrayList<>());

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", buildAuthHeader("egor@mail.ru", "123"));
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<User> entity = new HttpEntity<>(updateBody, headers);

        ResponseEntity<Map> response = testRestTemplate.exchange(
                "/user/" + user.getId(), HttpMethod.POST, entity, Map.class
        );
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("EgorUpdated", response.getBody().get("name"));
    }


    @Test
    public void testUpdateUser_InvalidPassword() {
        User user = new User();
        user.setName("egor");
        user.setEmail("egor@mail.ru");
        user.setPassword(passwordEncoder.encode("123"));
        user.setAuthenticated(true);
        user.setRole(Role.USER);
        user.setBookings(new ArrayList<>());
        user = userRepository.save(user);

        User updateBody = new User();
        updateBody.setName("EgorUpdated");
        updateBody.setEmail("egor@mail.ru");
        updateBody.setPassword("newpass");
        updateBody.setAuthenticated(true);
        updateBody.setRole(Role.USER);
        updateBody.setBookings(new ArrayList<>());

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", buildAuthHeader("egor@mail.ru", "wrongpass"));
        HttpEntity<User> entity = new HttpEntity<>(updateBody, headers);

        ResponseEntity<String> response = testRestTemplate.exchange("/user/profile/me", HttpMethod.POST, entity, String.class);
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }

    @Test
    public void testUpdateUser_NotAuthenticated() {
        User user = new User();
        user.setName("egor");
        user.setEmail("egor@mail.ru");
        user.setPassword(passwordEncoder.encode("123"));
        user.setAuthenticated(false);
        user.setRole(Role.USER);
        user.setBookings(new ArrayList<>());
        user = userRepository.save(user);

        User updateBody = new User();
        updateBody.setName("EgorUpdated");
        updateBody.setEmail("egor@mail.ru");
        updateBody.setPassword("newpass");
        updateBody.setAuthenticated(false);
        updateBody.setRole(Role.USER);
        updateBody.setBookings(new ArrayList<>());

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", buildAuthHeader("egor@mail.ru", "123"));
        HttpEntity<User> entity = new HttpEntity<>(updateBody, headers);

        ResponseEntity<String> response = testRestTemplate.exchange("/user/profile/me", HttpMethod.POST, entity, String.class);
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }

    @Test
    public void testDeleteUser_Success() {
        User user = new User();
        user.setName("egor");
        user.setEmail("egor@mail.ru");
        user.setPassword(passwordEncoder.encode("123"));
        user.setAuthenticated(true);
        user.setRole(Role.USER);
        user.setBookings(new ArrayList<>());
        user = userRepository.save(user);

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", buildAuthHeader("egor@mail.ru", "123"));
        HttpEntity<Void> entity = new HttpEntity<>(headers);

        // !!! /user/profile/me вместо /user/{id}
        ResponseEntity<String> response = testRestTemplate.exchange("/user/profile/me", HttpMethod.DELETE, entity, String.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertFalse(userRepository.findById(user.getId()).isPresent());
    }

    @Test
    public void testDeleteUser_NotAuthenticated() {
        User user = new User();
        user.setName("egor");
        user.setEmail("egor@mail.ru");
        user.setPassword(passwordEncoder.encode("123"));
        user.setAuthenticated(false);
        user.setRole(Role.USER);
        user.setBookings(new ArrayList<>());
        user = userRepository.save(user);

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", buildAuthHeader("egor@mail.ru", "123"));
        HttpEntity<Void> entity = new HttpEntity<>(headers);

        ResponseEntity<String> response = testRestTemplate.exchange("/user/profile/me", HttpMethod.DELETE, entity, String.class);
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }

    @Test
    public void testDeleteUser_NotExists() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", buildAuthHeader("egor@mail.ru", "123"));
        HttpEntity<Void> entity = new HttpEntity<>(headers);

        ResponseEntity<String> response = testRestTemplate.exchange("/user/profile/me", HttpMethod.DELETE, entity, String.class);
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }
}
