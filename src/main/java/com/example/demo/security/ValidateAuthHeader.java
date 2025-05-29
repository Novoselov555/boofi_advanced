package com.example.demo.security;

import com.example.demo.entity.User;
import com.example.demo.exception.IncorrectCredentialsException;
import com.example.demo.exception.UserNotFoundException;
import com.example.demo.repository.UserRepository;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Base64;

@Component
@RequiredArgsConstructor
public class ValidateAuthHeader {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public void isValid(String authHeader, Long id) throws UserNotFoundException {
        String[] parts = getCreds(authHeader);
        String email = extractEmail(parts);
        String encodedPassword = extractPassword(parts);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IncorrectCredentialsException("Не аутентифицированные действия!"));

        if (!user.getId().equals(id)) {
            throw new IncorrectCredentialsException("Не аутентифицированные действия!");
        }

        if (!passwordEncoder.matches(encodedPassword, user.getPassword())) {
            throw new IncorrectCredentialsException("Не аутентифицированные действия!");
        }

        if (!user.isAuthenticated()) {
            throw new IncorrectCredentialsException("Не аутентифицированные действия!");
        }
    }

    public String extractEmail(String[] parts) {
        return parts[0];
    }

    public String extractPassword(String[] parts) {
        return parts[1];
    }

    public String[] getCreds(String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Basic ")) {
            throw new IncorrectCredentialsException("Не аутентифицированные действия!");
        }
        authHeader = authHeader.substring(6);
        String decodedHeader = new String(Base64.getDecoder().decode(authHeader)).trim();
        String[] parts = decodedHeader.split(":");
        if (parts.length < 2) throw new IncorrectCredentialsException("Неверный токен");
        return parts;
    }
}
