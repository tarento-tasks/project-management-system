package com.example.project_management_backend.Controller;
import com.example.project_management_backend.DTO.ApiResponse;
import com.example.project_management_backend.DTO.LoginRequest;
import com.example.project_management_backend.DTO.LoginResponse;
import com.example.project_management_backend.Service.AuthService;
import com.example.project_management_backend.config.JwtUtil;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.project_management_backend.Model.User;
import com.example.project_management_backend.Repository.UserRepository;
import com.example.project_management_backend.Service.AuthService;
import com.example.project_management_backend.config.JwtUtil;
import com.example.project_management_backend.DTO.LoginRequest;
import com.example.project_management_backend.DTO.LoginResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "http://localhost:5173") // Allow only requests from this origin

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
public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest loginRequest) {

return ResponseEntity.ok(authService.login(loginRequest));
}



    @PostMapping("/logout")
public ResponseEntity<ApiResponse<String>> logout(@RequestHeader("Authorization") String token) {
    if (token == null || !token.startsWith("Bearer ")) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ApiResponse<>(400, "Invalid token format", null));
    }

    String jwt = token.substring(7);
    jwtUtil.invalidateToken(jwt); 
    return ResponseEntity.ok(new ApiResponse<>(200, "Logout successful", null));
}
}
