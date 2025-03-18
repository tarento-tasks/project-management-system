package com.example.project_management_backend.Controller;

import com.example.project_management_backend.DTO.ApiResponse;
import com.example.project_management_backend.DTO.LoginRequest;
import com.example.project_management_backend.DTO.LoginResponse;
import com.example.project_management_backend.Service.AuthService;
import com.example.project_management_backend.config.JwtUtil;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;
    private final JwtUtil jwtUtil;  

    public AuthController(AuthService authService, JwtUtil jwtUtil) {
        this.authService = authService;
        this.jwtUtil = jwtUtil;  
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


    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<String>> logout(@RequestHeader("Authorization") String token) {
        if (token == null || !token.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse<>(400, "Invalid token format", null));
        }

        String jwt = token.substring(7); 
        jwtUtil.invalidateToken(jwt);  
        System.out.println("Token invalidated successfully: " + jwt); 
        return ResponseEntity.ok(new ApiResponse<>(200, "Logout successful", null));
    }
}
