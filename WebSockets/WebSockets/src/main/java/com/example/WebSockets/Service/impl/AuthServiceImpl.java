package com.example.WebSockets.Service.impl;

import com.example.WebSockets.Model.User;
import com.example.WebSockets.Repositories.UserRepository;
import com.example.WebSockets.Service.AuthService;
import com.example.WebSockets.Service.JwtService;
import com.example.WebSockets.dtos.LoginRequest;
import com.example.WebSockets.dtos.RegisterRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    @Override
    public void register(RegisterRequest request) {

        User user = User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .phoneNumber(request.getPhoneNumber())
                .password(passwordEncoder.encode(request.getPassword()))
                .build();

        userRepository.save(user);
    }

    @Override
    public String login(LoginRequest request) {

        User user = userRepository
                .findByPhoneNumber(request.getPhoneNumber())
                .orElseThrow(() ->
                        new RuntimeException("Invalid phone number or password")
                );

        if (!passwordEncoder.matches(
                request.getPassword(),
                user.getPassword()
        )) {
            throw new RuntimeException(
                    "Invalid phone number or password"
            );
        }

        return jwtService.generateToken(user.getPhoneNumber());
    }
}