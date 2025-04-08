package com.example.project_management_backend.Controller;
import com.example.project_management_backend.Model.User;
import com.example.project_management_backend.DTO.ApiResponse;
import com.example.project_management_backend.DTO.ProjectEnrollmentDto;
import com.example.project_management_backend.Model.Project;
import com.example.project_management_backend.Model.ProjectEnrollment;
import com.example.project_management_backend.Repository.UserRepository;
import com.example.project_management_backend.Service.ProjectEnrollmentService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.Authentication;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.UUID;

@RestController
@CrossOrigin(origins = "http://localhost:5173")
@RequestMapping("/api/project-enrollment")
public class ProjectEnrollmentController {

    private final ProjectEnrollmentService enrollmentService;

    private final UserRepository userRepository;

    // Constructor-based injection for both services
    public ProjectEnrollmentController(ProjectEnrollmentService enrollmentService, UserRepository userRepository) {
        this.enrollmentService = enrollmentService;
        this.userRepository = userRepository;
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
    @RequestParam(value = "enrollmentId", required = false) UUID enrollmentId,
    @RequestParam(value = "studentId", required = false) UUID studentId
) {
    if (enrollmentId != null) {
        ProjectEnrollment enrollment = enrollmentService.getEnrollmentById(enrollmentId);
        return ResponseEntity.ok(
            new ApiResponse<>(HttpStatus.OK.value(), "Enrollment fetched successfully", enrollment)
        );
    } 

    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    User user = userRepository.findByEmailAndDeletedAtIsNull(authentication.getName())
            .orElseThrow(() -> new RuntimeException("User not found"));

    if (user.getRole().getRoleName().equals("STUDENT")) {
        List<Project> approvedProjects = enrollmentService.getApprovedProjectsForStudent(user.getUserId());
        return ResponseEntity.ok(
            new ApiResponse<>(HttpStatus.OK.value(), "Approved projects fetched successfully", approvedProjects)
        );
    }

    if (user.getRole().getRoleName().equals("ADMIN")) {
        if (studentId != null) {
            List<Project> approvedProjects = enrollmentService.getApprovedProjectsForStudent(studentId);
            return ResponseEntity.ok(
                new ApiResponse<>(HttpStatus.OK.value(), "Approved projects for student fetched successfully", approvedProjects)
            );
        }
        List<ProjectEnrollment> enrollments = enrollmentService.getAllEnrollments();
        return ResponseEntity.ok(
            new ApiResponse<>(HttpStatus.OK.value(), "All enrollments fetched successfully", enrollments)
        );
    }

    return ResponseEntity.status(HttpStatus.FORBIDDEN).body(
        new ApiResponse<>(HttpStatus.FORBIDDEN.value(), "Access denied", null)
    );
}


@GetMapping("/approved-students")
@PreAuthorize("hasAnyRole('ADMIN', 'MENTOR')")
public ResponseEntity<ApiResponse<List<User>>> getApprovedStudentsForProject(
        @RequestParam UUID projectId) {
    List<User> students = enrollmentService.getApprovedStudentsForProject(projectId);
    return ResponseEntity.ok(
        new ApiResponse<>(HttpStatus.OK.value(), "Approved students fetched successfully", students)
    );
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