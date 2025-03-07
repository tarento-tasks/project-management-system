package com.example.project_management_backend.Controller;

import com.example.project_management_backend.DTO.UserDTO;
import com.example.project_management_backend.Model.Role;
import com.example.project_management_backend.Model.User;
import com.example.project_management_backend.Service.RoleService;
import com.example.project_management_backend.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private RoleService roleService; // ✅ Inject RoleService

    @GetMapping
    public List<UserDTO> getAllUsers() {
        return userService.getAllUsers();
    }

    @GetMapping("/{id}")
    public Optional<UserDTO> getUserById(@PathVariable UUID id) {
        return userService.getUserById(id);
    }

    @GetMapping("/email/{email}")
    public Optional<UserDTO> getUserByEmail(@PathVariable String email) {
        return userService.getUserByEmail(email);
    }

    @PostMapping(consumes = "multipart/form-data")
    public User createUser(
            @RequestParam String email,
            @RequestParam String password,
            @RequestParam String name,
            @RequestParam(required = false) String dob,
            @RequestParam(required = false) MultipartFile image,
            @RequestParam(required = false) String previousWork,
            @RequestParam(required = false) String qualifications,
            @RequestParam UUID role_id  // ✅ Expect UUID for role
    ) {

        System.out.println("Received image: " + (image != null ? image.getOriginalFilename() : "No image"));
        
        User user = new User();
        user.setEmail(email);
        user.setPassword(password);
        user.setName(name);
        user.setDob(dob);
        user.setPreviousWork(previousWork);
        user.setQualifications(qualifications);

        // ✅ Fetch Role entity using role_id
        Optional<Role> role = roleService.getRoleById(role_id)
                .map(roleDTO -> new Role(roleDTO.getRoleId(), roleDTO.getRoleName()));

        if (role.isPresent()) {
            user.setRole(role.get()); // ✅ Assign Role object to User
        } else {
            throw new RuntimeException("Invalid Role ID: " + role_id);
        }

        try {
            if (image != null && !image.isEmpty()) {
                user.setImages(image.getBytes()); // ✅ Store image as BLOB
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return userService.saveUser(user);
    }

    @PutMapping("/{id}")
public ResponseEntity<UserDTO> updateUser(
        @PathVariable UUID id,
        @RequestParam(required = false) String email,
        @RequestParam(required = false) String name,
        @RequestParam(required = false) String dob,
        @RequestParam(required = false) MultipartFile image,
        @RequestParam(required = false) String previousWork,
        @RequestParam(required = false) String qualifications,
        @RequestParam(required = false) UUID role_id
) {
    Optional<User> optionalUser = userService.getUserEntityById(id); // ✅ FIXED!
    if (optionalUser.isEmpty()) {
        return ResponseEntity.notFound().build();
    }

    User user = optionalUser.get();
    if (email != null) user.setEmail(email);
    if (name != null) user.setName(name);
    if (dob != null) user.setDob(dob);
    if (previousWork != null) user.setPreviousWork(previousWork);
    if (qualifications != null) user.setQualifications(qualifications);

    if (role_id != null) {
        Optional<Role> role = roleService.getRoleById(role_id)
                .map(roleDTO -> new Role(roleDTO.getRoleId(), roleDTO.getRoleName()));
        role.ifPresent(user::setRole);
    }

    try {
        if (image != null && !image.isEmpty()) {
            user.setImages(image.getBytes());
        }
    } catch (IOException e) {
        e.printStackTrace();
    }

    User updatedUser = userService.saveUser(user);
    UserDTO updatedUserDTO = userService.convertToDTO(updatedUser);
    
    return ResponseEntity.ok(updatedUserDTO);
}




    @DeleteMapping("/{id}")
    public void deleteUser(@PathVariable UUID id) {
        userService.deleteUser(id);
    }
}


