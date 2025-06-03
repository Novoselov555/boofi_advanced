package com.example.demo.exception.coworking;

public class CoworkingNotFoundException extends RuntimeException{
    public CoworkingNotFoundException(String message) {
        super(message);
    }
}
