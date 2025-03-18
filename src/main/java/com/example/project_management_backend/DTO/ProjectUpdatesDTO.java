package com.example.project_management_backend.DTO;

import lombok.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProjectUpdatesDTO {

    private Long updateId;
    //private UUID userId;
    private UUID projectId; // ✅ Use UUID instead of Project object
    private String updateText;
    private LocalDateTime createdAt;
}
