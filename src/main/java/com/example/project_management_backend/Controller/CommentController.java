package com.example.project_management_backend.Controller;

import com.example.project_management_backend.DTO.CommentDTO;
import com.example.project_management_backend.Service.CommentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;
import java.util.UUID;


@RestController
@CrossOrigin(origins = "http://localhost:5173")
@RequestMapping("/api/tasks/{taskId}/comments")
public class CommentController {

    private final CommentService commentService;

    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

   
    @PostMapping
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<CommentDTO> addComment(@PathVariable UUID taskId,
                                                 @RequestBody CommentDTO commentDTO) {
        CommentDTO createdComment = commentService.addComment(taskId, commentDTO.getComment());
        return ResponseEntity.ok(createdComment);
    }


  
    @GetMapping

    public ResponseEntity<List<CommentDTO>> getComments(@PathVariable UUID taskId) {
        List<CommentDTO> comments = commentService.getCommentsByTaskId(taskId);
        return ResponseEntity.ok(comments);
    }
}