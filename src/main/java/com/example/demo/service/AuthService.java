package com.example.demo.service;

import com.example.demo.dto.LoginRequest;
import com.example.demo.dto.RegisterRequest;
import com.example.demo.entity.Role;
import com.example.demo.entity.User;
import com.example.demo.exception.IncorrectCredentialsException;
import com.example.demo.exception.UserAlreadyExistsException;
import com.example.demo.repository.UserRepository;
import jakarta.annotation.PostConstruct;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Base64;
import java.util.HashMap;

@Service
@AllArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    @PostConstruct
    public void init() {
        User admin = new User();
        admin.setName("admin");
        admin.setEmail("admin@boofi.com");
        admin.setPassword(passwordEncoder.encode("admin123"));
        admin.setRole(Role.ADMIN);
        admin.setAuthenticated(true);
        userRepository.save(admin);
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
