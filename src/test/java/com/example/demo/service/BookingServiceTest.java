package com.example.demo.service;

import com.example.demo.dto.BookingDto;
import com.example.demo.entity.Booking;
import com.example.demo.entity.BookingStatus;
import com.example.demo.entity.Place;
import com.example.demo.entity.User;
import com.example.demo.exception.booking.BookingNotFoundException;
import com.example.demo.exception.booking.IncorrectBookingException;
import com.example.demo.repository.BookingRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class BookingServiceTest {

    @Mock
    BookingRepository bookingRepository;
    @Mock
    UserService userService;
    @Mock
    PlaceService placeService;

    @InjectMocks
    BookingService bookingService;

    @Test
    void testFindBookingByBookingId_found() {
        Booking booking = new Booking();
        booking.setId(1L);
        Mockito.when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));
        assertEquals(booking, bookingService.findBookingByBookingId(1L));
    }

    @Test
    void testFindBookingByBookingId_notFound() {
        Mockito.when(bookingRepository.findById(2L)).thenReturn(Optional.empty());
        assertThrows(BookingNotFoundException.class, () -> bookingService.findBookingByBookingId(2L));
    }

    @Test
    void testGetAllBookings() {
        List<Booking> bookings = List.of(new Booking(), new Booking());
        Mockito.when(bookingRepository.findAll()).thenReturn(bookings);
        assertEquals(bookings, bookingService.getAllBookings());
    }

    @Test
    void testBookAPlace_successful() {
        BookingDto dto = new BookingDto();
        dto.setTimeStart(LocalDateTime.now().plusHours(1));
        dto.setTimeEnd(LocalDateTime.now().plusHours(2));

        User user = new User();
        Place place = new Place();
        Mockito.when(userService.findById(1L)).thenReturn(user);
        Mockito.when(placeService.getPlaceById(2L)).thenReturn(place);
        Mockito.when(bookingRepository.getAllBookingsByPlaceId(2L)).thenReturn(Collections.emptyList());
        Mockito.when(bookingRepository.save(Mockito.any())).thenAnswer(i -> i.getArgument(0));

        Booking booking = bookingService.bookAPlace(1L, 2L, dto);
        assertEquals(user, booking.getUser());
        assertEquals(place, booking.getPlace());
        assertEquals(BookingStatus.BOOKED, booking.getStatus());
    }
}
