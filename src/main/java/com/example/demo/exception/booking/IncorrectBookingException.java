package com.example.demo.exception.booking;

public class IncorrectBookingException extends RuntimeException {
    public IncorrectBookingException(String message) {
        super(message);
    }
}
