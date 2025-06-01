package com.example.demo.controller;

import com.example.demo.dto.UserDto;
import com.example.demo.entity.User;
import com.example.demo.security.ValidateAuthHeader;
import com.example.demo.service.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {
    private final AdminService adminService;
    private final ValidateAuthHeader validateAuthHeader;

    @GetMapping("/users")
    public ResponseEntity<?> getUsers(@RequestHeader("Authorization") String authHeader) {
        validateAuthHeader.isAdminValid(authHeader);
        return ResponseEntity
                .ok()
                .body(adminService.getUsers());
    }

    @PostMapping("/users{id}")
    public ResponseEntity<?> updateById(@RequestHeader("Authorization") String authHeader, @PathVariable Long id, @RequestBody UserDto userDto) {
        validateAuthHeader.isAdminValid(authHeader);
        User recievedUser = adminService.updateById(id, userDto);
        return ResponseEntity
                .ok()
                .body(recievedUser);
    }

    @DeleteMapping("users/{id}")
    public ResponseEntity<?> deleteById(@RequestHeader("Authorization") String authHeader, @PathVariable Long id) {
        validateAuthHeader.isAdminValid(authHeader);
        adminService.deleteById(id);
        return ResponseEntity
                .ok()
                .build();
    }

    @DeleteMapping("users")
    public ResponseEntity<?> deleteAll(@RequestHeader("Authorization") String authHeader) {
        validateAuthHeader.isAdminValid(authHeader);
        adminService.deleteAllUsers();
        return ResponseEntity
                .ok()
                .build();
    }
}
