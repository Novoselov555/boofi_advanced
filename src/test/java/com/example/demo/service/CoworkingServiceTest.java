package com.example.demo.service;

import com.example.demo.entity.Coworking;
import com.example.demo.exception.coworking.CoworkingNotFoundException;
import com.example.demo.repository.CoworkingRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class CoworkingServiceTest {
    @Mock
    CoworkingRepository coworkingRepository;
    @InjectMocks
    CoworkingService coworkingService;

    @Test
    void testGetAllCoworkings() {
        List<Coworking> list = List.of(new Coworking(), new Coworking());
        Mockito.when(coworkingRepository.findAll()).thenReturn(list);
        assertEquals(list, coworkingService.getAllCoworkings());
    }

    @Test
    void testGetCoworkingById_found() {
        Coworking c = new Coworking();
        Mockito.when(coworkingRepository.findById(3L)).thenReturn(Optional.of(c));
        assertEquals(c, coworkingService.getCoworkingById(3L));
    }

    @Test
    void testGetCoworkingById_notFound() {
        Mockito.when(coworkingRepository.findById(9L)).thenReturn(Optional.empty());
        assertThrows(CoworkingNotFoundException.class, () -> coworkingService.getCoworkingById(9L));
    }
}
