package com.example.project_management_backend.Repository;


import com.example.project_management_backend.Model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {
    List<User> findByRole_RoleName(String roleName); // Fetch users by role name
    
    List<User> findByDeletedAtIsNull();
    Optional<User> findByEmailAndDeletedAtIsNull(String email);
    Optional<User> findByUserIdAndDeletedAtIsNull(UUID userId);
}