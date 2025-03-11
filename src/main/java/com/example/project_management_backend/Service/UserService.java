package com.example.project_management_backend.Service;

import com.example.project_management_backend.DTO.UserDTO;
import com.example.project_management_backend.Model.Role;
import com.example.project_management_backend.Model.User;
import com.example.project_management_backend.Repository.UserRepository;
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

    // ✅ **Create User (Admin Only)**
    public User createUser(String email, String password, String name, String dob,
                           MultipartFile image, String previousWork, String qualifications, UUID roleId) throws IOException {
        if (userRepository.findByEmail(email).isPresent()) {
            throw new RuntimeException("Email already exists.");
        }

        Role role = roleService.getRoleById(roleId)
                .map(roleDTO -> new Role(roleDTO.getRoleId(), roleDTO.getRoleName()))
                .orElseThrow(() -> new RuntimeException("Invalid role ID"));

        User user = new User();
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));
        user.setName(name);
        user.setDob(dob);
        user.setPreviousWork(previousWork);
        user.setQualifications(qualifications);
        user.setRole(role);
        user.setCreatedAt(LocalDateTime.now());
        user.setModifiedAt(LocalDateTime.now());

        if (image != null && !image.isEmpty()) {
            user.setImages(image.getBytes());
        }

        return userRepository.save(user);
    }

    // ✅ **Convert User Entity to DTO**
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

    // ✅ **Get All Users (Admin Only)**
    public List<UserDTO> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    // ✅ **Get User By ID**
    public Optional<UserDTO> getUserById(UUID id) {
        return userRepository.findById(id).map(this::convertToDTO);
    }

    // ✅ **Get User By Email (Admin Only)**
    public Optional<UserDTO> getUserByEmail(String email) {
        return userRepository.findByEmail(email).map(this::convertToDTO);
    }

    // ✅ **Update User Profile (For Students & Mentors - Limited Fields)**
    public User updateUser(UUID id, String dob, String previousWork, String qualifications, MultipartFile image, String password) {
        Optional<User> optionalUser = userRepository.findById(id);
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();

            if (dob != null) user.setDob(dob);
            if (previousWork != null) user.setPreviousWork(previousWork);
            if (qualifications != null) user.setQualifications(qualifications);
            
            if (password != null && !password.isEmpty()) {
                user.setPassword(passwordEncoder.encode(password));
            }

            try {
                if (image != null && !image.isEmpty()) {
                    user.setImages(image.getBytes());
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            user.setModifiedAt(LocalDateTime.now());
            return userRepository.save(user);
        } else {
            throw new RuntimeException("User not found with ID: " + id);
        }
    }

    // ✅ **Admin - Update Any User (Includes Email, Name, and Role)**
    public User updateUserByAdmin(UUID id, String email, String name, String dob, String previousWork, 
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

            user.setModifiedAt(LocalDateTime.now());
            return userRepository.save(user);
        } else {
            throw new RuntimeException("User not found with ID: " + id);
        }
    }

    // ✅ **Soft Delete User (Admin Only)**
    public void softDeleteUser(UUID id) {
        Optional<User> optionalUser = userRepository.findById(id);
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            user.setDeletedAt(LocalDateTime.now());
            userRepository.save(user);
        } else {
            throw new RuntimeException("User not found with ID: " + id);
        }
    }
}
