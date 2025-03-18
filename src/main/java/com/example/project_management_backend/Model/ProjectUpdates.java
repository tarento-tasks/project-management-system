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

    private UUID userId; // Foreign key (Ensure user exists)

    @ManyToOne
    @JoinColumn(name = "project_id", nullable = false) // Maps to Project's ID column
    private Project projectId; // ✅ Renamed from 'project' to 'projectId'

    private String updateText;

    @Lob
    private byte[] updateImage; // ✅ Store PDF/Image as byte array

    private LocalDateTime createdAt;
}
