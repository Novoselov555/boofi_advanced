package com.example.demo.controller;

import com.example.demo.entity.User;
import com.example.demo.security.ValidateAuthHeader;
import com.example.demo.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@Tag(name = "User", description = "User API")
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final ValidateAuthHeader validateAuthHeader;

    @Operation(summary = "Изменение личных данных пользователя")
    @PostMapping("/me")
    public ResponseEntity<?> update(@RequestHeader("Authorization") String authHeader, @RequestBody User userBody) {
        Long id = validateAuthHeader.isValid(authHeader);
        User user = userService.update(id, userBody);
        return ResponseEntity
                .ok()
                .body(user);
    }

    @PatchMapping("/me")
    public ResponseEntity<?> logout(@RequestHeader("Authorization") String authHeader) {
        Long id = validateAuthHeader.isValid(authHeader);
        User user = userService.logout(id);
        return ResponseEntity
                .ok()
                .body(user);
    }

    @DeleteMapping("/me")
    public ResponseEntity<?> delete(@RequestHeader("Authorization") String authHeader) {
        Long id = validateAuthHeader.isValid(authHeader);
        userService.delete(id);
        return ResponseEntity.ok().build();
    }
}
