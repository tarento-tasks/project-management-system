package com.example.project_management_backend.Controller;

import com.example.project_management_backend.DTO.ApiResponse;
import com.example.project_management_backend.DTO.FeedbackDTO;
import com.example.project_management_backend.Service.FeedbackService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/tasks/{taskId}/feedback")
public class FeedbackController {

    private final FeedbackService feedbackService;

    public FeedbackController(FeedbackService feedbackService) {
        this.feedbackService = feedbackService;
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('MENTOR', 'ADMIN')")
    public ResponseEntity<ApiResponse<FeedbackDTO>> addFeedback(
        @PathVariable UUID taskId,
        @RequestBody FeedbackDTO feedbackDTO
    ) {
        FeedbackDTO createdFeedback = feedbackService.addFeedback(taskId, feedbackDTO.getMentorId(), feedbackDTO.getFeedback());
        return ResponseEntity.ok(
            new ApiResponse<>(HttpStatus.OK.value(), "Feedback added successfully", createdFeedback)
        );
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('MENTOR', 'ADMIN','STUDENT')")
    public ResponseEntity<ApiResponse<List<FeedbackDTO>>> getFeedback(@PathVariable UUID taskId) {
        List<FeedbackDTO> feedbackList = feedbackService.getFeedbackByTaskId(taskId);
        return ResponseEntity.ok(
            new ApiResponse<>(HttpStatus.OK.value(), "Feedback fetched successfully", feedbackList)
        );
    }
}