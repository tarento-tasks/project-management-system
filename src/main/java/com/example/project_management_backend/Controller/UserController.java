package com.example.project_management_backend.Controller;

import com.example.project_management_backend.DTO.UserDTO;
import com.example.project_management_backend.Model.User;
import com.example.project_management_backend.Service.UserService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    
    @GetMapping
    public ResponseEntity<?> getUsers(@RequestParam(required = false) UUID userId,
                                      @RequestParam(required = false) String email) {
        if (userId != null) {
            return userService.getUserById(userId)
                    .map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        }
        if (email != null) {
            return userService.getUserByEmail(email)
                    .map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        }
        return ResponseEntity.ok(userService.getAllUsers());
    }

    
    @PostMapping
    public ResponseEntity<?> createOrUpdateUser(
            @RequestParam(required = false) UUID userId,
            @RequestParam String email,
            @RequestParam(required = false) String password,
            @RequestParam String name,
            @RequestParam String dob,
            @RequestParam(required = false) MultipartFile image,
            @RequestParam(required = false) String previousWork,
            @RequestParam(required = false) String qualifications,
            @RequestParam(required = false) UUID roleId) {

        try {
            User user = userService.createOrUpdateUser(userId, email, password, name, dob, image, previousWork, qualifications, roleId);
            return ResponseEntity.ok(userService.convertToDTO(user));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> softDeleteUser(@PathVariable UUID id) {
        boolean deleted = userService.deleteUser(id);
        return deleted ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }
}
