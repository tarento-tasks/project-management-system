package com.example.project_management_backend.Repository;


import com.example.project_management_backend.Model.StuTask;
import com.example.project_management_backend.Model.StuTaskId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface StuTaskRepository extends JpaRepository<StuTask, StuTaskId> {
    List<StuTask> findByTaskTaskId(UUID taskId);
    boolean existsById_StudentIdAndId_TaskIdAndDeletedAtIsNull(UUID studentId, UUID taskId);
}


