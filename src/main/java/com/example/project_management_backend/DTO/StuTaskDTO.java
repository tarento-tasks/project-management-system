package com.example.project_management_backend.DTO;

import lombok.Data;

import java.util.UUID;

@Data
public class StuTaskDTO {
    private UUID studentId;
    private UUID taskId;
}