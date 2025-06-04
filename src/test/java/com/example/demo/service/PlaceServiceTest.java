package com.example.demo.service;

import com.example.demo.entity.Place;
import com.example.demo.exception.place.PlaceNotFoundException;
import com.example.demo.repository.PlaceRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class PlaceServiceTest {
    @Mock
    PlaceRepository placeRepository;
    @InjectMocks
    PlaceService placeService;

    @Test
    void testGetPlaceById_found() {
        Place place = new Place();
        Mockito.when(placeRepository.findById(5L)).thenReturn(Optional.of(place));
        assertEquals(place, placeService.getPlaceById(5L));
    }

    @Test
    void testGetPlaceById_notFound() {
        Mockito.when(placeRepository.findById(42L)).thenReturn(Optional.empty());
        assertThrows(PlaceNotFoundException.class, () -> placeService.getPlaceById(42L));
    }
}
