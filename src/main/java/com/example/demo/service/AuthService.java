package com.example.demo.service;

import com.example.demo.dto.LoginRequest;
import com.example.demo.dto.RegisterRequest;
import com.example.demo.entity.Coworking;
import com.example.demo.entity.Place;
import com.example.demo.entity.Role;
import com.example.demo.entity.User;
import com.example.demo.exception.auth.IncorrectCredentialsException;
import com.example.demo.exception.auth.UserAlreadyExistsException;
import com.example.demo.repository.CoworkingRepository;
import com.example.demo.repository.PlaceRepository;
import com.example.demo.repository.UserRepository;
import jakarta.annotation.PostConstruct;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;

@Service
@AllArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final CoworkingRepository coworkingRepository;
    private final PlaceRepository placeRepository;

    @PostConstruct
    public void init() {
        // Хардкодим админа в бд
        if (userRepository.count() == 0) {
            User admin = new User();
            admin.setName("admin");
            admin.setEmail("admin@boofi.com");
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setRole(Role.ADMIN);
            admin.setAuthenticated(true);
            userRepository.save(admin);
        }

        // Хардкодим записи коворкингов
        if (coworkingRepository.count() == 0) {
            Coworking coworking1 = new Coworking(null, "Коворкинг 4 этаж", "Общее место для бота рядом с VK", new ArrayList<>());
            Coworking coworking2 = new Coworking(null, "Коворкинг 8 этаж", "Общее место для бота рядом с T-банком", new ArrayList<>());
            Coworking coworking3 = new Coworking(null, "Коворкинг 4 этаж", "Общее место в VK", new ArrayList<>());

            coworkingRepository.save(coworking1);
            coworkingRepository.save(coworking2);
            coworkingRepository.save(coworking3);

            for (int i = 1; i <= 8; i++) {
                Place place = new Place();
                place.setSeatId(i);
                place.setCoworking(coworking1);
                placeRepository.save(place);
            }

            for (int i = 1; i <= 8; i++) {
                Place place = new Place();
                place.setSeatId(i);
                place.setCoworking(coworking2);
                placeRepository.save(place);
            }

            for (int i = 1; i <= 8; i++) {
                Place place = new Place();
                place.setSeatId(i);
                place.setCoworking(coworking3);
                placeRepository.save(place);
            }
        }
    }

    public HashMap<String, String> getIdAndRole(User user) {
        HashMap<String, String> data = new HashMap<>();
        data.put("id", String.valueOf(user.getId()));
        data.put("role", String.valueOf(user.getRole()));
        return data;
    }

    public HashMap<String, String> register(RegisterRequest registerRequest) {
        if (userRepository.existsByEmail(registerRequest.getEmail())){
            throw new UserAlreadyExistsException("Пользователь с такой почтой уже существует");
        }

        User user = new User();
        user.setName(registerRequest.getName());
        user.setEmail(registerRequest.getEmail());
        user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        user.setAuthenticated(true);
        user.setRole(Role.USER);
        userRepository.save(user);

        HashMap<String, String> data = getIdAndRole(user);

        String credentials = user.getEmail() + ":" + registerRequest.getPassword();
        String token = Base64.getEncoder().encodeToString(credentials.getBytes());
        data.put("token", token);

        return data;
    }

    public HashMap<String, String> login(LoginRequest loginRequest) {
        User user = userRepository.findByEmail(loginRequest.getEmail())
                .orElseThrow(() -> new IncorrectCredentialsException("Неверный логин или пароль"));

        if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
            throw new IncorrectCredentialsException("Неверный логин или пароль");
        }
        user.setAuthenticated(true);

        HashMap<String, String> data = getIdAndRole(user);
        String credentials = user.getEmail() + ":" + loginRequest.getPassword();
        String token = Base64.getEncoder().encodeToString(credentials.getBytes());
        data.put("token", token);

        return data;
    }
}
