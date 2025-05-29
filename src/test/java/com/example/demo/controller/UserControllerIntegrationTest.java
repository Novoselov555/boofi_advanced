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

import java.util.Base64;
import java.util.Optional;

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
        User user = new User(null, "egor", "egor@mail.ru", passwordEncoder.encode("123"), true, Role.USER);
        user = userRepository.save(user);

        User updateBody = new User(null, "EgorUpdated", "egor@mail.ru", "newpass", true, Role.USER);

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", buildAuthHeader("egor@mail.ru", "123"));
        HttpEntity<User> entity = new HttpEntity<>(updateBody, headers);

        ResponseEntity<User> response = testRestTemplate.exchange("/user/" + user.getId(), HttpMethod.POST, entity, User.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("EgorUpdated", response.getBody().getName());
    }

    @Test
    public void testUpdateUser_InvalidPassword() {
        User user = new User(null, "egor", "egor@mail.ru", passwordEncoder.encode("123"), true, Role.USER);
        user = userRepository.save(user);

        User updateBody = new User(null, "EgorUpdated", "egor@mail.ru", "newpass", true, Role.USER);

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", buildAuthHeader("egor@mail.ru", "wrongpass"));
        HttpEntity<User> entity = new HttpEntity<>(updateBody, headers);

        ResponseEntity<String> response = testRestTemplate.exchange("/user/" + user.getId(), HttpMethod.POST, entity, String.class);
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }

    @Test
    public void testUpdateUser_NotAuthenticated() {
        User user = new User(null, "egor", "egor@mail.ru", passwordEncoder.encode("123"), false, Role.USER);
        user = userRepository.save(user);

        User updateBody = new User(null, "EgorUpdated", "egor@mail.ru", "newpass", false, Role.USER);

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", buildAuthHeader("egor@mail.ru", "123"));
        HttpEntity<User> entity = new HttpEntity<>(updateBody, headers);

        ResponseEntity<String> response = testRestTemplate.exchange("/user/" + user.getId(), HttpMethod.POST, entity, String.class);
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }

    @Test
    public void testUpdateUser_OtherUserId() {
        User user1 = new User(null, "egor", "egor@mail.ru", passwordEncoder.encode("123"), true, Role.USER);
        user1 = userRepository.save(user1);
        User user2 = new User(null, "petr", "petr@mail.ru", passwordEncoder.encode("321"), true, Role.USER);
        user2 = userRepository.save(user2);

        User updateBody = new User(null, "EgorUpdated", "egor@mail.ru", "newpass", true, Role.USER);

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", buildAuthHeader("egor@mail.ru", "123"));
        HttpEntity<User> entity = new HttpEntity<>(updateBody, headers);

        ResponseEntity<String> response = testRestTemplate.exchange("/user/" + user2.getId(), HttpMethod.POST, entity, String.class);
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }

    @Test
    public void testUpdateUser_UserNotExists() {
        User updateBody = new User(null, "EgorUpdated", "egor@mail.ru", "newpass", true, Role.USER);

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", buildAuthHeader("egor@mail.ru", "123"));
        HttpEntity<User> entity = new HttpEntity<>(updateBody, headers);

        ResponseEntity<String> response = testRestTemplate.exchange("/user/99999", HttpMethod.POST, entity, String.class);
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }

    @Test
    public void testDeleteUser_Success() {
        User user = new User(null, "egor", "egor@mail.ru", passwordEncoder.encode("123"), true, Role.USER);
        user = userRepository.save(user);

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", buildAuthHeader("egor@mail.ru", "123"));
        HttpEntity<Void> entity = new HttpEntity<>(headers);

        ResponseEntity<String> response = testRestTemplate.exchange("/user/" + user.getId(), HttpMethod.DELETE, entity, String.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertFalse(userRepository.findById(user.getId()).isPresent());
    }

    @Test
    public void testDeleteUser_OtherUserId() {
        User user1 = new User(null, "egor", "egor@mail.ru", passwordEncoder.encode("123"), true, Role.USER);
        user1 = userRepository.save(user1);
        User user2 = new User(null, "petr", "petr@mail.ru", passwordEncoder.encode("321"), true, Role.USER);
        user2 = userRepository.save(user2);

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", buildAuthHeader("egor@mail.ru", "123"));
        HttpEntity<Void> entity = new HttpEntity<>(headers);

        ResponseEntity<String> response = testRestTemplate.exchange("/user/" + user2.getId(), HttpMethod.DELETE, entity, String.class);
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }

    @Test
    public void testDeleteUser_NotAuthenticated() {
        User user = new User(null, "egor", "egor@mail.ru", passwordEncoder.encode("123"), false, Role.USER);
        user = userRepository.save(user);

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", buildAuthHeader("egor@mail.ru", "123"));
        HttpEntity<Void> entity = new HttpEntity<>(headers);

        ResponseEntity<String> response = testRestTemplate.exchange("/user/" + user.getId(), HttpMethod.DELETE, entity, String.class);
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }

    @Test
    public void testDeleteUser_NotExists() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", buildAuthHeader("egor@mail.ru", "123"));
        HttpEntity<Void> entity = new HttpEntity<>(headers);

        ResponseEntity<String> response = testRestTemplate.exchange("/user/99999", HttpMethod.DELETE, entity, String.class);
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }
}
