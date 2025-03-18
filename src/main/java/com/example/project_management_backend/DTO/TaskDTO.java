package com.example.project_management_backend.DTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data

public class TaskDTO {
    private UUID taskId;
    private String taskName;
    private byte[] attachments;
    private LocalDateTime createdAt;
    private LocalDateTime dueDate;
    private String studentStatus;
    private String completeStatus;
    private LocalDateTime modifiedAt;
    private String openStatus;
    private LocalDateTime deletedAt;
    private String taskObjective;
    private UUID modifiedBy;
    private UUID projectId; 


    
    
}