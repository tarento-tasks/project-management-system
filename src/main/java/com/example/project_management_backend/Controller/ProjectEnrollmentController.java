package com.example.project_management_backend.Controller;

import com.example.project_management_backend.DTO.ApiResponse;
import com.example.project_management_backend.DTO.ProjectEnrollmentDto;
import com.example.project_management_backend.Model.ProjectEnrollment;
import com.example.project_management_backend.Service.ProjectEnrollmentService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;
import java.util.UUID;

@RestController
@CrossOrigin(origins = "http://localhost:5173")
@RequestMapping("/api/project-enrollment")
public class ProjectEnrollmentController {

    private final ProjectEnrollmentService enrollmentService;

    public ProjectEnrollmentController(ProjectEnrollmentService enrollmentService) {
        this.enrollmentService = enrollmentService;
    }

   
 
  

    @PostMapping
@PreAuthorize("hasAnyRole('STUDENT', 'ADMIN')")
public ResponseEntity<ApiResponse<ProjectEnrollment>> createOrUpdateEnrollment(
    @RequestParam(value = "enrollmentId", required = false) UUID enrollmentId,
    @RequestBody ProjectEnrollmentDto dto
) {
    try {
        ProjectEnrollment enrollment;
        if (enrollmentId != null) {
            enrollment = enrollmentService.updateEnrollmentStatus(enrollmentId, dto.getStatus());
            return ResponseEntity.ok(new ApiResponse<>(HttpStatus.OK.value(), "Enrollment status updated successfully", enrollment));
        } else {
            enrollment = enrollmentService.enrollStudent(dto);
            return ResponseEntity.ok(new ApiResponse<>(HttpStatus.OK.value(), "Enrollment created successfully", enrollment));
        }
    } catch (IllegalStateException e) {
        return ResponseEntity.badRequest().body(new ApiResponse<>(HttpStatus.BAD_REQUEST.value(), e.getMessage(), null));
    }
}

   
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'STUDENT')")
    public ResponseEntity<ApiResponse<?>> getEnrollments(
        @RequestParam(value = "enrollmentId", required = false) UUID enrollmentId
    ) {
        if (enrollmentId != null) {
            
            ProjectEnrollment enrollment = enrollmentService.getEnrollmentById(enrollmentId);
            return ResponseEntity.ok(
                new ApiResponse<>(HttpStatus.OK.value(), "Enrollment fetched successfully", enrollment)
            );
        } else {
           
            if (SecurityContextHolder.getContext().getAuthentication().getAuthorities().stream()
                .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ROLE_ADMIN"))) {
                List<ProjectEnrollment> enrollments = enrollmentService.getAllEnrollments();
                return ResponseEntity.ok(
                    new ApiResponse<>(HttpStatus.OK.value(), "All enrollments fetched successfully", enrollments)
                );
            } else {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(
                    new ApiResponse<>(HttpStatus.FORBIDDEN.value(), "Access denied", null)
                );
            }
        }
    }

    @DeleteMapping("/{enrollmentId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'STUDENT')")
    public ResponseEntity<ApiResponse<Void>> softDeleteEnrollment(@PathVariable UUID enrollmentId) {
        enrollmentService.softDeleteEnrollment(enrollmentId);
        return ResponseEntity.ok(
            new ApiResponse<>(HttpStatus.OK.value(), "Enrollment soft deleted successfully", null)
        );
    }
}