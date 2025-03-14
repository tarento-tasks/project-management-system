package com.example.project_management_backend.Service;

import com.example.project_management_backend.Model.User;
import com.example.project_management_backend.Repository.UserRepository;
import com.example.project_management_backend.config.JwtUtil;
import com.example.project_management_backend.DTO.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;  
        this.jwtUtil = jwtUtil;
    }

    
    
  

    public LoginResponse login(LoginRequest loginRequest) {

        User user = userRepository.findByEmailAndDeletedAtIsNull(loginRequest.getEmail()).orElseThrow(() -> new RuntimeException("User not found"));
        System.out.println("Entered password:" + loginRequest.getPassword());
        System.out.println("Stored Hashed Password: " + user.getPassword());
    
        if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid password");    
        }
        
        String token = jwtUtil.generateToken(user);

        LoginResponse loginResponse = new LoginResponse();
        loginResponse.setToken(token);
        return loginResponse;
        
    }



} 