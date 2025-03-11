package com.example.project_management_backend.Model;

import jakarta.persistence.*;
import lombok.*;
import java.util.UUID;

import java.time.LocalDateTime;

@Entity
@Table(name = "project_updates")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProjectUpdates {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long updateId;

    private UUID userId; // Foreign key

    @ManyToOne
    @JoinColumn(name = "project_id", nullable = false)
    private Project project; // Foreign key reference to Project

    private String updateText;

    @Lob
    private byte[] updateImage; // Store image as byte array

    private LocalDateTime createdAt;
}

