package com.example.project_management_backend.Controller;

import com.example.project_management_backend.DTO.ApiResponse;
import com.example.project_management_backend.DTO.CommentDTO;
import com.example.project_management_backend.Service.CommentService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/tasks/{taskId}/comments")
public class CommentController {

    private final CommentService commentService;

    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    @PostMapping
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<ApiResponse<CommentDTO>> addComment(
        @PathVariable UUID taskId,
        @RequestBody CommentDTO commentDTO
    ) {
        
        UUID userId = UUID.fromString(SecurityContextHolder.getContext().getAuthentication().getName());

        CommentDTO createdComment = commentService.addComment(taskId, userId, commentDTO.getComment());
        return ResponseEntity.ok(
            new ApiResponse<>(HttpStatus.OK.value(), "Comment added successfully", createdComment)
        );
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<CommentDTO>>> getComments(@PathVariable UUID taskId) {
        List<CommentDTO> comments = commentService.getCommentsByTaskId(taskId);
        return ResponseEntity.ok(
            new ApiResponse<>(HttpStatus.OK.value(), "Comments fetched successfully", comments)
        );
    }
}