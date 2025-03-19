package com.example.project_management_backend.DTO;

import java.time.LocalDateTime;
import java.util.UUID;

public class FeedbackDTO {

    private UUID feedbackId;
    private String feedback;
    private UUID taskId;
   
    private LocalDateTime createdAt;

    public FeedbackDTO(UUID feedbackId, String feedback, UUID taskId, LocalDateTime createdAt) {
        this.feedbackId = feedbackId;
        this.feedback = feedback;
        this.taskId = taskId;
     
        this.createdAt = createdAt;
    }


    public UUID getFeedbackId() {
        return feedbackId;
    }

    public void setFeedbackId(UUID feedbackId) {
        this.feedbackId = feedbackId;
    }

    public String getFeedback() {
        return feedback;
    }

    public void setFeedback(String feedback) {
        this.feedback = feedback;
    }

    public UUID getTaskId() {
        return taskId;
    }

    public void setTaskId(UUID taskId) {
        this.taskId = taskId;
    }

 
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}