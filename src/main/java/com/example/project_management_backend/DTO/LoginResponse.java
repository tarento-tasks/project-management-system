package com.example.project_management_backend.DTO;
import java.util.UUID;
import lombok.Data;

@Data
public class LoginResponse {
    private String token;
    private String role;
    private UUID userId;

}
