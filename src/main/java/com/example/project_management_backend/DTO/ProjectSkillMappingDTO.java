package com.example.project_management_backend.DTO;

import lombok.*;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProjectSkillMappingDTO {
    private UUID projectId;
    private UUID skillId;
}
