package com.example.demo.exception.place;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Collections;
import java.util.Map;

@RestControllerAdvice
public class PlaceExceptionHandler {
    @ExceptionHandler(PlaceNotFoundException.class)
    public ResponseEntity<Map<String, String>> handlerPlaceNotFoundException(PlaceNotFoundException ex) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(Collections.singletonMap("message", ex.getMessage()));
    }
}
