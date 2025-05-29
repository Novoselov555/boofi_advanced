package com.example.demo.controller;

import com.example.demo.dto.LoginRequest;
import com.example.demo.dto.RegisterRequest;
import com.example.demo.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.Map;

@RestController
@Tag(name = "Auth", description = "Auth API")
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @Operation(summary = "Регистрация пользователя")
    @PostMapping("/register")
    public ResponseEntity<Map<String, String>> register(@RequestBody RegisterRequest registerRequest) {
        String credentials = authService.register(registerRequest);
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Basic " + credentials);
        return ResponseEntity
                .ok()
                .headers(headers)
                .body(Collections.singletonMap("credentials", credentials));

    }

    @Operation(summary = "Аутентификация пользователя")
    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(@RequestBody LoginRequest loginRequest) {
        String credentials = authService.login(loginRequest);
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Basic " + credentials);
        return ResponseEntity
                .ok()
                .headers(headers)
                .body(Collections.singletonMap("credentials", credentials));
    }
}
