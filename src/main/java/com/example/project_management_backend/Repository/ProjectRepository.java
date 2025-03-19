package com.example.project_management_backend.Repository;

import com.example.project_management_backend.Model.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.UUID;
import java.util.Optional;

@Repository
public interface ProjectRepository extends JpaRepository<Project, UUID> {
    List<Project> findByDeletedAtIsNull();
    
    Optional<Project> findByProjectIdAndDeletedAtIsNull(UUID projectId);
    boolean existsByTitleAndRepoAndDeletedAtIsNull(String title, String repo);

}
