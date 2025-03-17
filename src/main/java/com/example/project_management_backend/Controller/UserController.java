package com.example.project_management_backend.Controller;

import com.example.project_management_backend.DTO.UserDTO;
import com.example.project_management_backend.Model.User;
import com.example.project_management_backend.Service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
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



    @PostMapping(consumes = "multipart/form-data")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> createUser(
            @RequestParam String email,
            @RequestParam String password,
            @RequestParam String name,
            @RequestParam(required = false) String dob,
            @RequestParam(required = false) MultipartFile image,
            @RequestParam(required = false) String previousWork,
            @RequestParam(required = false) String qualifications,
            @RequestParam UUID roleId) throws IOException {

        User user = userService.createUser(email, password, name, dob, image, previousWork, qualifications, roleId);
        return ResponseEntity.ok("User created successfully");
    }


    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public ResponseEntity<List<UserDTO>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @PreAuthorize("hasAnyRole('STUDENT', 'MENTOR', 'ADMIN')")
    @GetMapping("/{id}")
    public ResponseEntity<UserDTO> getUserById(@PathVariable UUID id) {
        Optional<UserDTO> user = userService.getUserById(id);
        return user.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }


    @PreAuthorize("hasAnyRole('STUDENT', 'MENTOR', 'ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<UserDTO> updateUser(
            @PathVariable UUID id,
            @RequestParam(required = false) String dob,
            @RequestParam(required = false) String previousWork,
            @RequestParam(required = false) String qualifications,
            @RequestParam(required = false) MultipartFile image,
            @RequestParam(required = false) String password) {

        User updatedUser = userService.updateUser(id, dob, previousWork, qualifications, image, password);
        return ResponseEntity.ok(userService.convertToDTO(updatedUser));
    }


    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/admin/{id}")
    public ResponseEntity<UserDTO> updateUserByAdmin(
            @PathVariable UUID id,
            @RequestParam(required = false) String email,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String dob,
            @RequestParam(required = false) String previousWork,
            @RequestParam(required = false) String qualifications,
            @RequestParam(required = false) UUID roleId,
            @RequestParam(required = false) MultipartFile image,
            @RequestParam(required = false) String password) {

        User updatedUser = userService.updateUserByAdmin(id, email, name, dob, previousWork, qualifications, roleId, image, password);
        return ResponseEntity.ok(userService.convertToDTO(updatedUser));
    }


    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> softDeleteUser(@PathVariable UUID id) {
        userService.softDeleteUser(id);
        return ResponseEntity.noContent().build();
    }


    @PreAuthorize("hasAnyRole('STUDENT', 'MENTOR', 'ADMIN')")
    @GetMapping("/email")
    public ResponseEntity<UserDTO> getUserByEmail(@RequestParam String email) {
        Optional<UserDTO> user = userService.getUserByEmail(email);
        return user.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}