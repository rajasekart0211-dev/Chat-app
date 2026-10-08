package com.example.WebSockets.dtos;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginRequest {

    private String phoneNumber;

    private String password;
}
