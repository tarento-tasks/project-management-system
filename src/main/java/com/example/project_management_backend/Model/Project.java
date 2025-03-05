package com.example.project_management_backend.Model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "projects")
public class Project {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID projectId;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String objective;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false)
    private LocalDate dueDate; // Project completion deadline

    @Column(nullable = false)
    private String criteria; // Qualification criteria

    @Column(nullable = false)
    private String repo; // Repository link

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    private LocalDateTime modifiedAt;

    @Column(nullable = false)
    private LocalDate lastDate; // Last date to apply

    @Column(nullable = false)
    private boolean openStatus; // true = Open, false = Closed

    private LocalDateTime deletedAt;

    @ManyToOne
    @JoinColumn(name = "mentor_id", nullable = false)
    private User mentor; // Assigned mentor (Foreign Key to User Table)

}
