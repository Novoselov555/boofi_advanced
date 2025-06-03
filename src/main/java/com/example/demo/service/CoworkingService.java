package com.example.demo.service;


import com.example.demo.entity.Coworking;
import com.example.demo.exception.coworking.CoworkingNotFoundException;
import com.example.demo.repository.CoworkingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CoworkingService {
    private final CoworkingRepository coworkingRepository;

    public List<Coworking> getAllCoworkings() {
        return coworkingRepository.findAll();
    }

    public Coworking getCoworkingById(Long coworkingId) {
        return coworkingRepository.findById(coworkingId).orElseThrow(() -> new CoworkingNotFoundException("Коворкинг не найден: " + coworkingId));
    }
}
