package com.example.WebSockets.dtos;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegisterRequest {

    private String name;

    private String email;

    private String phoneNumber;

    private String password;
}