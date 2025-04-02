package com.example.project_management_backend.Service;

import com.example.project_management_backend.DTO.RoleDTO;
import com.example.project_management_backend.DTO.UserDTO;
import com.example.project_management_backend.Model.Role;
import com.example.project_management_backend.Model.User;
import com.example.project_management_backend.Repository.UserRepository;

import jakarta.transaction.Transactional;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
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

    @Transactional
    public List<UserDTO> getAllUsers() {
        List<User> users = userRepository.findByDeletedAtIsNull();
        return users.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    

    @Transactional
    public List<UserDTO> getUsersByRole(String role) {
        String roleName = role.toUpperCase(); // Spring Security format
        
        List<User> users = userRepository.findByRoleName(roleName);
        
        return users.stream()
                .map(user -> {
                    UserDTO dto = new UserDTO();
                    dto.setUserId(user.getUserId());
                    dto.setName(user.getName());
                  
                    dto.setRoleId(user.getRole().getRoleId()); // Only setting roleId
                    
                    return dto;
                })
                .collect(Collectors.toList());
    }
    @Transactional
    public Optional<UserDTO> getUserById(UUID id) {
        return userRepository.findByUserIdAndDeletedAtIsNull(id)
                .map(this::convertToDTO);
    }

    public Optional<UserDTO> getUserByEmail(String email) {
        return userRepository.findByEmailAndDeletedAtIsNull(email)
                .map(this::convertToDTO);
    }

   
    @Transactional
    public User createOrUpdateUser(UUID id, String email, String password, String name, String dob, 
                                   MultipartFile image, String previousWork, String qualifications, UUID roleId) {

        
        Optional<User> existingUser = userRepository.findByEmailAndDeletedAtIsNull(email);
        if (existingUser.isPresent() && (id == null || !existingUser.get().getUserId().equals(id))) {
            throw new RuntimeException("User with this email already exists!");
        }

        User user;

        
        if (id == null) {
            user = new User();
            user.setPassword(passwordEncoder.encode(password));
            user.setCreatedAt(LocalDateTime.now());
        } else {
            
            user = userRepository.findByUserIdAndDeletedAtIsNull(id)
                    .orElseThrow(() -> new RuntimeException("User not found or has been deleted!"));
        }

        user.setEmail(email);
        user.setName(name);
        user.setDob(dob);
        user.setPreviousWork(previousWork);
        user.setQualifications(qualifications);
        user.setModifiedAt(LocalDateTime.now());

        
        if (roleId != null) {
            Optional<Role> role = roleService.getRoleById(roleId)
                    .map(roleDTO -> new Role(roleDTO.getRoleId(), roleDTO.getRoleName()));
            role.ifPresent(user::setRole);
        }

        
        try {
            if (image != null && !image.isEmpty()) {
                user.setImages(image.getBytes());
            }
        } catch (IOException e) {
            throw new RuntimeException("Error processing image", e);
        }

        return userRepository.save(user);
    }

    
    public boolean deleteUser(UUID id) {
        Optional<User> userOpt = userRepository.findByUserIdAndDeletedAtIsNull(id);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            user.setDeletedAt(LocalDateTime.now());
            userRepository.save(user);
            return true;
        }
        return false;
    }
}