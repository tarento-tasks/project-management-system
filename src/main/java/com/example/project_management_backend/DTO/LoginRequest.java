package com.example.project_management_backend.DTO;

import lombok.Data;

@Data
public class LoginRequest {
    private String email;
    private String password;
}