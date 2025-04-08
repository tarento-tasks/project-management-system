package com.example.project_management_backend.Repository;

import com.example.project_management_backend.Model.ProjectSkillMapping;
import com.example.project_management_backend.Model.ProjectSkillMappingId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ProjectSkillMappingRepository extends JpaRepository<ProjectSkillMapping, ProjectSkillMappingId> {
    List<ProjectSkillMapping> findByProject_ProjectId(UUID projectId);
    List<ProjectSkillMapping> findBySkill_SkillId(UUID skillId);
    List<ProjectSkillMapping> findBySkill_SkillIdIn(List<UUID> skillIds);
}
