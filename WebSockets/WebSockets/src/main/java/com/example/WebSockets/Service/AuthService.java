package com.example.WebSockets.Service;

import com.example.WebSockets.dtos.LoginRequest;
import com.example.WebSockets.dtos.RegisterRequest;

public interface AuthService {

    void register(RegisterRequest request);

    String login(LoginRequest request);
}
