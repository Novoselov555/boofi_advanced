package com.example.demo.controller;

import com.example.demo.dto.UserDto;
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

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@Testcontainers
public class AdminControllerIntegrationTest extends DataBaseConnect {
    @Autowired
    private TestRestTemplate restTemplate;
    @Autowired
    private PasswordEncoder passwordEncoder;
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

    private User createAdmin(String email, String password) {
        User admin = new User(null, "admin", email, passwordEncoder.encode(password), true, Role.ADMIN, new ArrayList<>(), new ArrayList<>());
        return userRepository.save(admin);
    }

    private User createUser(String name, String email, String password) {
        User user = new User(null, name, email, passwordEncoder.encode(password), true, Role.USER, new ArrayList<>(), new ArrayList<>());
        return userRepository.save(user);
    }

    @Test
    public void testGetUsers_Success() {
        createAdmin("admin@mail.ru", "adminpass");
        createUser("user1", "user1@mail.ru", "pass1");
        createUser("user2", "user2@mail.ru", "pass2");

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", buildAuthHeader("admin@mail.ru", "adminpass"));
        HttpEntity<Void> entity = new HttpEntity<>(headers);

        ResponseEntity<User[]> response = restTemplate.exchange("/admin/users", HttpMethod.GET, entity, User[].class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(2, response.getBody().length); // только USERS
    }

    @Test
    public void testGetUsers_NotAdmin() {
        createUser("user", "user@mail.ru", "userpass");

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", buildAuthHeader("user@mail.ru", "userpass"));
        HttpEntity<Void> entity = new HttpEntity<>(headers);

        ResponseEntity<String> response = restTemplate.exchange("/admin/users", HttpMethod.GET, entity, String.class);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }

    @Test
    public void testUpdateById_Success() {
        User admin = createAdmin("admin@mail.ru", "adminpass");
        User user = createUser("Egor", "egor@mail.ru", "userpass");

        UserDto userDto = new UserDto();
        userDto.setName("Egor Updated");
        userDto.setEmail("updated@mail.ru");
        userDto.setRole(Role.ADMIN);

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", buildAuthHeader("admin@mail.ru", "adminpass"));
        HttpEntity<UserDto> entity = new HttpEntity<>(userDto, headers);

        ResponseEntity<User> response = restTemplate.exchange("/admin/users" + user.getId(), HttpMethod.POST, entity, User.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Egor Updated", response.getBody().getName());
        assertEquals("updated@mail.ru", response.getBody().getEmail());
        assertEquals(Role.ADMIN, response.getBody().getRole());
    }

    @Test
    public void testUpdateById_NotAdmin() {
        User user = createUser("Egor", "egor@mail.ru", "userpass");
        UserDto userDto = new UserDto();
        userDto.setName("Egor Updated");

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", buildAuthHeader("egor@mail.ru", "userpass"));
        HttpEntity<UserDto> entity = new HttpEntity<>(userDto, headers);

        ResponseEntity<String> response = restTemplate.exchange("/admin/users" + user.getId(), HttpMethod.POST, entity, String.class);
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }

    @Test
    public void testUpdateById_UserNotFound() {
        createAdmin("admin@mail.ru", "adminpass");

        UserDto userDto = new UserDto();
        userDto.setName("Nobody");

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", buildAuthHeader("admin@mail.ru", "adminpass"));
        HttpEntity<UserDto> entity = new HttpEntity<>(userDto, headers);

        ResponseEntity<String> response = restTemplate.exchange("/admin/users99999", HttpMethod.POST, entity, String.class);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    public void testDeleteById_Success() {
        createAdmin("admin@mail.ru", "adminpass");
        User user = createUser("Egor", "egor@mail.ru", "userpass");

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", buildAuthHeader("admin@mail.ru", "adminpass"));
        HttpEntity<Void> entity = new HttpEntity<>(headers);

        ResponseEntity<String> response = restTemplate.exchange("/admin/users/" + user.getId(), HttpMethod.DELETE, entity, String.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertFalse(userRepository.findById(user.getId()).isPresent());
    }

    @Test
    public void testDeleteById_NotAdmin() {
        User user = createUser("Egor", "egor@mail.ru", "userpass");

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", buildAuthHeader("egor@mail.ru", "userpass"));
        HttpEntity<Void> entity = new HttpEntity<>(headers);

        ResponseEntity<String> response = restTemplate.exchange("/admin/users/" + user.getId(), HttpMethod.DELETE, entity, String.class);
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }

    @Test
    public void testDeleteById_UserNotFound() {
        createAdmin("admin@mail.ru", "adminpass");

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", buildAuthHeader("admin@mail.ru", "adminpass"));
        HttpEntity<Void> entity = new HttpEntity<>(headers);

        ResponseEntity<String> response = restTemplate.exchange("/admin/users/99999", HttpMethod.DELETE, entity, String.class);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    public void testDeleteAll_Success() {
        createAdmin("admin@mail.ru", "adminpass");
        createUser("user1", "user1@mail.ru", "pass1");
        createUser("user2", "user2@mail.ru", "pass2");

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", buildAuthHeader("admin@mail.ru", "adminpass"));
        HttpEntity<Void> entity = new HttpEntity<>(headers);

        ResponseEntity<String> response = restTemplate.exchange("/admin/users", HttpMethod.DELETE, entity, String.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, userRepository.findAll().size());
    }

    @Test
    public void testDeleteAll_NotAdmin() {
        createUser("user1", "user1@mail.ru", "pass1");

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", buildAuthHeader("user1@mail.ru", "pass1"));
        HttpEntity<Void> entity = new HttpEntity<>(headers);

        ResponseEntity<String> response = restTemplate.exchange("/admin/users", HttpMethod.DELETE, entity, String.class);
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }
}
