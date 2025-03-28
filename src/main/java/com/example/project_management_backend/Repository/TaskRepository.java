package com.example.project_management_backend.Repository;

import com.example.project_management_backend.Model.Project;
import com.example.project_management_backend.Model.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface TaskRepository extends JpaRepository<Task, UUID> {
    List<Task> findByProject_ProjectId(UUID projectId); 
    Optional<Task> findByTaskIdAndDeletedAtIsNull(UUID taskId);
    boolean existsByTaskNameAndProject_ProjectId(String taskName, UUID projectId);
    

}