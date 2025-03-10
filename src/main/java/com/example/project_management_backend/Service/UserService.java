package com.example.project_management_backend.Service;
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
        List<User> projects = userRepository.findByDeletedAtIsNull();
        return projects.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    /*public Optional<UserDTO> getUserById(UUID id) {
        return userRepository.findById(id).map(this::convertToDTO); // ✅ Convert User -> UserDTO
    }*/
    public Optional<User> getUserEntityById(UUID id) {
        return userRepository.findById(id); 
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

    public User saveUser(User user) {
        return userRepository.save(user);
    }

    public User updateUser(UUID id, String email, String name, String dob, String previousWork, String qualifications, UUID roleId, MultipartFile image,  String password) {
        Optional<User> optionalUser = userRepository.findById(id);
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            user.setEmail(email);
            user.setName(name);
            user.setDob(dob);
            user.setPreviousWork(previousWork);
            user.setQualifications(qualifications);
            

            if (password != null && !password.isEmpty()) {
                user.setPassword(passwordEncoder.encode(password));
            }

            if (roleId != null) {
                Optional<Role> role = roleService.getRoleById(roleId).map(roleDTO -> new Role(roleDTO.getRoleId(), roleDTO.getRoleName()));
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

    public boolean deleteUser(UUID id) {
        Optional<User> userOpt = userRepository.findById(id);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            if (user.getDeletedAt() == null) {  
                user.setDeletedAt(LocalDateTime.now()); 
                userRepository.save(user);
                return true;
            }
        }
        return false;
        
    }
}
