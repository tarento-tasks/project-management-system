
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
    private LocalDate dueDate; 

    @Column(nullable = false)
    private String criteria; 

    @Column(nullable = false)
    private String repo; 

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    private LocalDateTime modifiedAt;

    @Column(nullable = false)
    private LocalDate lastDate; 

    @Column(nullable = false)
    private boolean openStatus; 

    private LocalDateTime deletedAt;

    @ManyToOne
    @JoinColumn(name = "mentor_id", nullable = false)
    private User mentor; 
}