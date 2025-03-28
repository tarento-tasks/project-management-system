package com.example.project_management_backend.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import com.example.project_management_backend.DTO.FeedbackDTO;
import com.example.project_management_backend.Service.FeedbackService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@CrossOrigin(origins = "http://localhost:5173")
@RequestMapping("/api/tasks/{taskId}/feedback")
public class FeedbackController {

    private final FeedbackService feedbackService;

    public FeedbackController(FeedbackService feedbackService) {
        this.feedbackService = feedbackService;
    }

    // Mentor: Post Feedback
    @PostMapping
    public ResponseEntity<FeedbackDTO> addFeedback(@PathVariable UUID taskId,
                                                   @RequestBody FeedbackDTO feedbackDTO) {
        FeedbackDTO createdFeedback = feedbackService.addFeedback(taskId, feedbackDTO.getMentorId(), feedbackDTO.getFeedback());
        return ResponseEntity.ok(createdFeedback);
    }

    // Student: Get Feedback by Task ID
    @GetMapping
    public ResponseEntity<List<FeedbackDTO>> getFeedback(@PathVariable UUID taskId) {
        List<FeedbackDTO> feedbackList = feedbackService.getFeedbackByTaskId(taskId);
        return ResponseEntity.ok(feedbackList);
    }
}
