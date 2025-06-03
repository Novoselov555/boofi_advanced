package com.example.demo.controller;

import com.example.demo.entity.Coworking;
import com.example.demo.security.ValidateAuthHeader;
import com.example.demo.service.CoworkingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Tag(name = "Coworking", description = "Coworking API")
@RequestMapping("/coworkings")
@RequiredArgsConstructor
public class CoworkingController {
    private final CoworkingService coworkingService;
    private final ValidateAuthHeader validateAuthHeader;

    @Operation(summary = "Получение всех коворкингов")
    @GetMapping
    public ResponseEntity<?> getAllCoworkings(@RequestHeader("Authorization") String authHeader) {
        validateAuthHeader.isValid(authHeader);
        List<Coworking> coworkings = coworkingService.getAllCoworkings();
        return ResponseEntity
                .ok()
                .body(coworkings);
    }

    @Operation(summary = "Получение коворкинга по coworkingId")
    @GetMapping("/{coworkingId}")
    public ResponseEntity<?> getCoworkingById(@RequestHeader("Authorization") String authHeader, @PathVariable Long coworkingId) {
        validateAuthHeader.isValid(authHeader);
        Coworking coworking = coworkingService.getCoworkingById(coworkingId);
        return ResponseEntity
                .ok()
                .body(coworking);
    }

}
