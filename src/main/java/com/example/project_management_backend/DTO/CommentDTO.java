package com.example.project_management_backend.DTO;

import java.time.LocalDateTime;
import java.util.UUID;

public class CommentDTO {

    private UUID commentId;
    private String comment;
    private UUID taskId;
    private LocalDateTime createdAt;

    
    public CommentDTO(UUID commentId, String comment, UUID taskId, LocalDateTime createdAt) {
        this.commentId = commentId;
        this.comment = comment;
        this.taskId = taskId;
        this.createdAt = createdAt;
    }

    public UUID getCommentId() {
        return commentId;
    }

    public void setCommentId(UUID commentId) {
        this.commentId = commentId;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
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