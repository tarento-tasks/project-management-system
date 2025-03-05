package com.example.project_management_backend.Model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID userId;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    private String name;
    private String dob; // Date of Birth (Can be stored as String or LocalDate)

    @Lob
    private byte[] images; // Stores image as a BLOB

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    private LocalDateTime modifiedAt;
    private LocalDateTime deletedAt;

    private String previousWork;
    private String qualifications;



    @ManyToOne
    @JoinColumn(name = "role_id", nullable = false)
    private Role role; // Foreign key reference to Role entity

    // Default Constructor


}
