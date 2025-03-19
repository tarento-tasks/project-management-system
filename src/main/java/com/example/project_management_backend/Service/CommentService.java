package com.example.project_management_backend.Service;

import com.example.project_management_backend.DTO.CommentDTO;
import com.example.project_management_backend.Model.Comment;
import com.example.project_management_backend.Model.Task;
import com.example.project_management_backend.Repository.CommentRepository;
import com.example.project_management_backend.Repository.TaskRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import com.example.project_management_backend.Exception.ResourceNotFoundException;
import java.util.Optional;
import com.example.project_management_backend.Exception.AccessDeniedException;



import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class CommentService {

    private final CommentRepository commentRepository;
    private final TaskRepository taskRepository;

    public CommentService(CommentRepository commentRepository, TaskRepository taskRepository) {
        this.commentRepository = commentRepository;
        this.taskRepository = taskRepository;
    }

    public CommentDTO addComment(UUID taskId, String commentText) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found with ID: " + taskId));


        Comment comment = new Comment();
        comment.setTask(task);
        //comment.setUserId(userId);
        comment.setComment(commentText);

        Comment savedComment = commentRepository.save(comment);
        return new CommentDTO(savedComment.getCommentId(), savedComment.getComment(),
                savedComment.getTask().getTaskId(),  savedComment.getCreatedAt());
    }
    @Transactional
    public List<CommentDTO> getCommentsByTaskId(UUID taskId) {

        Optional<Task> taskOptional = taskRepository.findById(taskId);
        if (taskOptional.isEmpty() || taskOptional.get().getDeletedAt() != null) {
            throw new ResourceNotFoundException("Task not found or has been deleted!");
        }



        List<Comment> comments = commentRepository.findByTask_TaskId(taskId);
        return comments.stream()
                .map(comment -> new CommentDTO(comment.getCommentId(), comment.getComment(),
                        comment.getTask().getTaskId(),  comment.getCreatedAt()))
                .collect(Collectors.toList());
    }
}