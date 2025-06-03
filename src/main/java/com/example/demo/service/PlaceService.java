package com.example.demo.service;

import com.example.demo.entity.Place;
import com.example.demo.exception.place.PlaceNotFoundException;
import com.example.demo.repository.PlaceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PlaceService {
    private final PlaceRepository placeRepository;

    public Place getPlaceById(Long placeId) {
        return placeRepository.findById(placeId)
                .orElseThrow(() -> new PlaceNotFoundException("Место не найдено: " + placeId));
    }
}
