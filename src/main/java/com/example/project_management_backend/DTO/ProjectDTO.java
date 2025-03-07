package com.example.project_management_backend.DTO;

import lombok.Data;
import java.time.LocalDate;

import java.util.UUID;

@Data
public class ProjectDTO {
    private UUID projectId;
    private String title;
    private String objective;
    private String description;
    private LocalDate dueDate;
    private String criteria;
    private String repo;
    private LocalDate lastDate;
    private boolean openStatus;
    private UUID mentorId; 
}

