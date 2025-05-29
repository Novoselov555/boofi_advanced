package com.example.demo.controller;

import com.example.demo.entity.User;
import com.example.demo.exception.IncorrectCredentialsException;
import com.example.demo.repository.UserRepository;
import com.example.demo.security.ValidateAuthHeader;
import com.example.demo.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@Tag(name = "User", description = "User API")
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final ValidateAuthHeader validateAuthHeader;
    private final UserRepository userRepository;

    @Operation(summary = "Изменение личных данных пользователя")
    @PostMapping("/{id}")
    public ResponseEntity<?> update(@RequestHeader("Authorization") String authHeader, @PathVariable Long id, @RequestBody User userBody) {
        validateAuthHeader.isValid(authHeader, id);
        User user = userService.update(id, userBody);
        return ResponseEntity
                .ok()
                .body(user);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@RequestHeader("Authorization") String authHeader, @PathVariable Long id) {
        validateAuthHeader.isValid(authHeader, id);
        userService.delete(id);
        return ResponseEntity.ok().build();
    }
}
