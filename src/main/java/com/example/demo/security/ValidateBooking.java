package com.example.demo.security;

import com.example.demo.entity.Booking;
import com.example.demo.entity.User;
import com.example.demo.exception.booking.BookingNotFoundException;
import com.example.demo.exception.booking.IncorrectBookingException;
import com.example.demo.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ValidateBooking {
    private final UserService userService;

    public void hasUserBooking(Long userId, Long bookingId) throws BookingNotFoundException{
        User user = userService.findById(userId);
        boolean hasBooking = false;
        for (Booking booking: user.getBookings()) {
            if (booking.getId().equals(bookingId)){
                hasBooking = true;
                break;
            }
        }
        if (!hasBooking) {
            throw new BookingNotFoundException("Ошибка бронирования");
        }
    }
}
