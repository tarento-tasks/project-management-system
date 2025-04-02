package com.example.project_management_backend.Repository;


import com.example.project_management_backend.Model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {
    
    List<User> findByRole_RoleName(String roleName); // Fetch users by role name
    Optional<User> findByEmail(String email);
    List<User> findByDeletedAtIsNull();
    Optional<User> findByEmailAndDeletedAtIsNull(String email);
    Optional<User> findByUserIdAndDeletedAtIsNull(UUID userId);
     @Query("SELECT u FROM User u JOIN u.role r WHERE r.roleName = :roleName AND u.deletedAt IS NULL")
    List<User> findByRoleName(@Param("roleName") String roleName);
}
