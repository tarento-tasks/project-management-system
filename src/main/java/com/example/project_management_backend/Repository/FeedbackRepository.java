package com.example.project_management_backend.Repository;

import com.example.project_management_backend.Model.Feedback;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;

public interface FeedbackRepository extends JpaRepository<Feedback, UUID> {
    List<Feedback> findByTask_TaskId(UUID taskId);
}
