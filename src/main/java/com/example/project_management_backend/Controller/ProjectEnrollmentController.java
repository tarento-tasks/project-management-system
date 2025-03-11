package com.example.project_management_backend.Controller;

import com.example.project_management_backend.DTO.ProjectEnrollmentDto;
import com.example.project_management_backend.Model.ProjectEnrollment;
import com.example.project_management_backend.Service.ProjectEnrollmentService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/project-enrollment")
public class ProjectEnrollmentController {

    private final ProjectEnrollmentService enrollmentService;

    public ProjectEnrollmentController(ProjectEnrollmentService enrollmentService) {
        this.enrollmentService = enrollmentService;
    }

    @PostMapping("/enroll")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<ProjectEnrollment> enrollStudent(@RequestBody ProjectEnrollmentDto dto) {
        return ResponseEntity.ok(enrollmentService.enrollStudent(dto));
    }

    @GetMapping("/")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<ProjectEnrollment>> getAllEnrollments() {
        return ResponseEntity.ok(enrollmentService.getAllEnrollments());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'STUDENT')")
    public ResponseEntity<ProjectEnrollment> getEnrollmentById(@PathVariable UUID id) {
        return ResponseEntity.ok(enrollmentService.getEnrollmentById(id));
    }

    @PutMapping("/{id}/update-status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ProjectEnrollment> updateEnrollmentStatus(@PathVariable UUID id, @RequestParam String status) {
        return ResponseEntity.ok(enrollmentService.updateEnrollmentStatus(id, status));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> softDeleteEnrollment(@PathVariable UUID id) {
        enrollmentService.softDeleteEnrollment(id);
        return ResponseEntity.ok("Enrollment soft deleted successfully.");
    }
}
