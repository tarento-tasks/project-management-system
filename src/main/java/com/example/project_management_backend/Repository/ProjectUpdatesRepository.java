package com.example.project_management_backend.Repository;

import com.example.project_management_backend.Model.ProjectUpdates;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.UUID;

import java.util.List;

@Repository
public interface ProjectUpdatesRepository extends JpaRepository<ProjectUpdates, Long> {
    List<ProjectUpdates> findByProject_ProjectId(UUID projectId);
}
