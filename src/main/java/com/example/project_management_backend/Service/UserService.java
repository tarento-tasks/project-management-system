package com.example.project_management_backend.Service;

import com.example.project_management_backend.DTO.UserDTO;
import com.example.project_management_backend.Model.Role;
import com.example.project_management_backend.Model.User;
import com.example.project_management_backend.Repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;


import java.io.IOException;
import java.util.Base64;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;


@Service
public class UserService {

    private final UserRepository userRepository;
    private final RoleService roleService;
    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(); 

    public UserService(UserRepository userRepository, RoleService roleService) {
        this.userRepository = userRepository;
        this.roleService = roleService;
    }

    public User saveUser(User user) {
       
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }



    public UserDTO convertToDTO(User user) {
        UUID roleId = (user.getRole() != null) ? user.getRole().getRoleId() : null;
        String base64Image = (user.getImages() != null)
                ? Base64.getEncoder().encodeToString(user.getImages())
                : null;
        return new UserDTO(
                user.getUserId(), user.getEmail(), user.getName(),
                user.getDob(), user.getPreviousWork(), user.getQualifications(),
                roleId, user.getCreatedAt(), user.getModifiedAt(), user.getDeletedAt(), base64Image
        );
    }

    public List<UserDTO> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

 
    public Optional<User> getUserEntityById(UUID id) {
        return userRepository.findById(id);
    }

    public Optional<UserDTO> getUserById(UUID id) {
        return userRepository.findById(id).map(this::convertToDTO); 
    }
    
    
    
    

    public Optional<UserDTO> getUserByEmail(String email) {
        return userRepository.findByEmail(email).map(this::convertToDTO);
    }

   

    public User updateUser(UUID id, String email, String name, String dob, String previousWork, 
                       String qualifications, UUID roleId, MultipartFile image, String password) {
    Optional<User> optionalUser = userRepository.findById(id);
    if (optionalUser.isPresent()) {
        User user = optionalUser.get();
        if (email != null) user.setEmail(email);
        if (name != null) user.setName(name);
        if (dob != null) user.setDob(dob);
        if (previousWork != null) user.setPreviousWork(previousWork);
        if (qualifications != null) user.setQualifications(qualifications);
        
        
        if (password != null && !password.isEmpty()) {
            user.setPassword(passwordEncoder.encode(password));
        }

        if (roleId != null) {
            Optional<Role> role = roleService.getRoleById(roleId).map(roleDTO -> 
                new Role(roleDTO.getRoleId(), roleDTO.getRoleName()));
            role.ifPresent(user::setRole);
        }

        try {
            if (image != null && !image.isEmpty()) {
                user.setImages(image.getBytes());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        return userRepository.save(user);
    } else {
        throw new RuntimeException("User not found with ID: " + id);
    }
}


    public void deleteUser(UUID id) {
        userRepository.deleteById(id);
    }
} 