package com.example.project_management_backend.Repository;

import com.example.project_management_backend.Model.ProjectUpdates;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.UUID;

@Repository
public interface ProjectUpdatesRepository extends JpaRepository<ProjectUpdates, Long> {
    List<ProjectUpdates> findByProjectId_ProjectId(UUID projectId);
}