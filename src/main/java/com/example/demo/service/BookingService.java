package com.example.demo.service;

import com.example.demo.dto.BookingDto;
import com.example.demo.entity.Booking;
import com.example.demo.entity.BookingStatus;
import com.example.demo.entity.Place;
import com.example.demo.entity.User;
import com.example.demo.exception.booking.BookingNotFoundException;
import com.example.demo.exception.booking.IncorrectBookingException;
import com.example.demo.repository.BookingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BookingService {
    private final BookingRepository bookingRepository;
    private final UserService userService;
    private final PlaceService placeService;

    //  Получение бронирования по bookingId
    public Booking findBookingByBookingId(Long bookingId) {
        return bookingRepository.findById(bookingId).orElseThrow(() -> new BookingNotFoundException("Ошибка бронирования"));
    }

    // Получение всех бронирований
    public List<Booking> getAllBookings() {
        return bookingRepository.findAll();
    }

    // Получение бронирований по месту
    public List<Booking> getBookingsByPlaceId(Long placeId) {
        return bookingRepository.getAllBookingsByPlaceId(placeId);
    }

    // Бронирование места
    public Booking bookAPlace(Long userId, Long placeId, BookingDto bookingDto) {
        Booking booking = new Booking();
        User user = userService.findById(userId);
        Place place = placeService.getPlaceById(placeId);
        List<Booking> bookedPlaces = getBookingsByPlaceId(placeId);

        //Проверка того, что время начала меньше времени конца
        if (bookingDto.getTimeStart().isAfter(bookingDto.getTimeEnd())) {
            throw new IncorrectBookingException("Время начала брони стоит позже, чем время конца бронирования");
        }

        // Проверка на то, что время начала бронирования больше, чем текущее
        if (bookingDto.getTimeStart().isBefore(LocalDateTime.now())) {
            throw new IncorrectBookingException("Время начала бронирования меньше, чем текущее");
        }

        if (bookedPlaces.isEmpty()) {
            booking.setUser(user);
            booking.setPlace(place);
            booking.setTimeStart(bookingDto.getTimeStart());
            booking.setTimeEnd(bookingDto.getTimeEnd());
            booking.setStatus(BookingStatus.BOOKED);
            return bookingRepository.save(booking);
        }

        // Проверка на удовлетворяющее нас место
        boolean flag = false;
        for (Booking b : bookedPlaces) {
            if (b.getStatus() != BookingStatus.BOOKED) {
                flag = true;
            } else {
                if (bookingDto.getTimeStart().isBefore(b.getTimeStart())
                        && bookingDto.getTimeEnd().isBefore(b.getTimeStart())
                        || bookingDto.getTimeStart().isAfter(b.getTimeEnd())
                        && bookingDto.getTimeEnd().isAfter(b.getTimeEnd())) {
                    flag = true;
                } else {
                    flag = false;
                    break;
                }
            }
        }
        if (flag) {
            booking.setUser(user);
            booking.setPlace(place);
            booking.setTimeStart(bookingDto.getTimeStart());
            booking.setTimeEnd(bookingDto.getTimeEnd());
            booking.setStatus(BookingStatus.BOOKED);
            return bookingRepository.save(booking);
        } else {
            throw new IncorrectBookingException("Не удалось забронировать место, оно занято другим в ваш промежуток");
        }
    }

    // Отмена бронирования пользователем
    public Booking cancelBooking(Long bookingId) {
        Booking booking = findBookingByBookingId(bookingId);
        booking.setStatus(BookingStatus.CANCELLED);
        return bookingRepository.save(booking);
    }

    // Перенос бронирования пользователем
    public Booking changeTime(Long userId, Long bookingId, BookingDto bookingDto) {
        Long placeId = findBookingByBookingId(bookingId).getId();
        cancelBooking(bookingId);
        Booking booking = bookAPlace(userId, placeId, bookingDto);
        return bookingRepository.save(booking);
    }
}
