package com.example.project_management_backend.DTO;

import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProjectUpdatesDTO {
    private Long updateId;
    private UUID userId;
    private UUID projectId;
    private String updateText;
    private MultipartFile updateImage;  // ✅ Changed from byte[] to MultipartFile
    private LocalDateTime createdAt;
}
