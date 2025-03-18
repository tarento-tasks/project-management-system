package com.example.project_management_backend.Controller;

import com.example.project_management_backend.DTO.ApiResponse;
import com.example.project_management_backend.DTO.LoginRequest;
import com.example.project_management_backend.DTO.LoginResponse;
import com.example.project_management_backend.Service.AuthService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

 
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponse>> login(@RequestBody LoginRequest loginRequest) {
        try {
            LoginResponse loginResponse = authService.login(loginRequest);
            return ResponseEntity.ok(new ApiResponse<>(HttpStatus.OK.value(), "Login successful", loginResponse));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ApiResponse<>(HttpStatus.UNAUTHORIZED.value(), "Invalid credentials", null));
        }
    }
}