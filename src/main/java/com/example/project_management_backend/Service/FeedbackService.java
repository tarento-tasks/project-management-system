package com.example.project_management_backend.Service;

import com.example.project_management_backend.DTO.FeedbackDTO;
import com.example.project_management_backend.Model.Feedback;
import com.example.project_management_backend.Model.Task;
import com.example.project_management_backend.Repository.FeedbackRepository;
import com.example.project_management_backend.Repository.TaskRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class FeedbackService {

    private final FeedbackRepository feedbackRepository;
    private final TaskRepository taskRepository;

    public FeedbackService(FeedbackRepository feedbackRepository, TaskRepository taskRepository) {
        this.feedbackRepository = feedbackRepository;
        this.taskRepository = taskRepository;
    }

    public FeedbackDTO addFeedback(UUID taskId, String feedbackText) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Task not found"));

        Feedback feedback = new Feedback();
        feedback.setTask(task);
       // feedback.setMentorId(mentorId);
        feedback.setFeedback(feedbackText);

        Feedback savedFeedback = feedbackRepository.save(feedback);
        return new FeedbackDTO(savedFeedback.getFeedbackId(), savedFeedback.getFeedback(),
                savedFeedback.getTask().getTaskId(),  savedFeedback.getCreatedAt());
    }
    @Transactional
    public List<FeedbackDTO> getFeedbackByTaskId(UUID taskId) {
        List<Feedback> feedbacks = feedbackRepository.findByTask_TaskId(taskId);
        return feedbacks.stream()
                .map(feedback -> new FeedbackDTO(feedback.getFeedbackId(), feedback.getFeedback(),
                        feedback.getTask().getTaskId(),feedback.getCreatedAt()))
                .collect(Collectors.toList());
    }
}

