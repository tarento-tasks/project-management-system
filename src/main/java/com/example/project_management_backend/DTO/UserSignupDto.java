package com.example.project_management_backend.DTO;



import lombok.Data;

@Data
public class UserSignupDto {
    private String email;
    private String password;
    private String name;
}