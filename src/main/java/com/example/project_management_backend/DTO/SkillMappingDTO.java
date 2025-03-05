package com.example.project_management_backend.DTO;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SkillMappingDTO {
    private String id;       // Mapping ID (UUID)
    private String userId;   // User ID
    private String skillId;  // Skill ID
}

