package com.example.project_management_backend.Repository;
 
import com.example.project_management_backend.Model.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;
import org.springframework.data.jpa.repository.Query;
 
 
 
import java.util.List;
import java.util.UUID;
 
@Repository
public interface TaskRepository extends JpaRepository<Task, UUID> {
    List<Task> findByProject_ProjectId(UUID projectId);
 
 
   List<Task> findByProject_ProjectIdAndDeletedAtIsNull(UUID projectId);
 
    @Query("SELECT t FROM Task t WHERE t.deletedAt IS NULL")
    List<Task> findAllActive();
 
    Optional<Task> findByTaskIdAndDeletedAtIsNull(UUID taskId);
 
    //boolean existsByTaskNameAndProject_ProjectId(String taskName, UUID projectId);
 
    boolean existsByTaskNameAndProject_ProjectIdAndDeletedAtIsNull(String taskName, UUID projectId);
 
    boolean existsByTaskNameAndProject_ProjectId(String taskName, UUID projectId);
}