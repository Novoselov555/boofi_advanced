package com.example.demo.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class BookingDto {
    @NotBlank
    private LocalDateTime timeStart;

    @NotBlank
    private LocalDateTime timeEnd;
}
