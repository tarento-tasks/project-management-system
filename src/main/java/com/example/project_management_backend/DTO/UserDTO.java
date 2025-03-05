package com.example.project_management_backend.DTO;

import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {
    private UUID userId;
    private String email;
    private String name;
    private String dob;
    private String previousWork;
    private String qualifications;
    private UUID roleId; // Store only the ID instead of the entire object
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;
    private LocalDateTime deletedAt;
    private String imageBase64; // ✅ Image as Base64 string

    // ✅ Explicitly define the constructor to match `convertToDTO()`

}
