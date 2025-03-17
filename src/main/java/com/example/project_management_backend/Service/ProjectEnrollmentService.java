package com.example.project_management_backend.Service;

import com.example.project_management_backend.DTO.ProjectEnrollmentDto;
import com.example.project_management_backend.Model.Project;
import com.example.project_management_backend.Model.ProjectEnrollment;
import com.example.project_management_backend.Model.User;
import com.example.project_management_backend.Repository.ProjectEnrollmentRepository;
import com.example.project_management_backend.Repository.ProjectRepository;
import com.example.project_management_backend.Repository.UserRepository;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class ProjectEnrollmentService {

    private final ProjectEnrollmentRepository enrollmentRepository;
    private final UserRepository userRepository;
    private final ProjectRepository projectRepository;

    public ProjectEnrollmentService(ProjectEnrollmentRepository enrollmentRepository, UserRepository userRepository, ProjectRepository projectRepository) {
        this.enrollmentRepository = enrollmentRepository;
        this.userRepository = userRepository;
        this.projectRepository = projectRepository;
    }

    public ProjectEnrollment enrollStudent(ProjectEnrollmentDto dto) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User student = userRepository.findByEmailAndDeletedAtIsNull(authentication.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!"STUDENT".equals(student.getRole().getRoleName())) {
            throw new AccessDeniedException("Only students can enroll in projects.");
        }

        Project project = projectRepository.findById(dto.getProjectId())
                .orElseThrow(() -> new RuntimeException("Project not found"));

        if (project.getDeletedAt() != null) {
            throw new RuntimeException("Cannot enroll in a deleted project.");
        }

       
        Optional<ProjectEnrollment> existingEnrollment = enrollmentRepository.findByStudent_UserIdAndProject_ProjectIdAndDeletedAtIsNull(student.getUserId(), project.getProjectId());
        if (existingEnrollment.isPresent()) {
            throw new IllegalStateException("Student is already enrolled in this project.");
        }

       
        ProjectEnrollment enrollment = new ProjectEnrollment();
        enrollment.setStudent(student);
        enrollment.setProject(project);
        enrollment.setStatus("PENDING");

        return enrollmentRepository.save(enrollment);
    }

    public List<ProjectEnrollment> getAllEnrollments() {
        return enrollmentRepository.findByProject_DeletedAtIsNullAndDeletedAtIsNull();
    }

    public ProjectEnrollment getEnrollmentById(UUID enrollmentId) {
        return enrollmentRepository.findByEnrollmentIdAndProject_DeletedAtIsNullAndDeletedAtIsNull(enrollmentId)
                .orElseThrow(() -> new RuntimeException("Enrollment not found or project is deleted"));
    }

    public ProjectEnrollment updateEnrollmentStatus(UUID enrollmentId, String status) {
        ProjectEnrollment enrollment = getEnrollmentById(enrollmentId);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User admin = userRepository.findByEmailAndDeletedAtIsNull(authentication.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!"ADMIN".equals(admin.getRole().getRoleName())) {
            throw new AccessDeniedException("Only admins can approve or reject enrollments.");
        }

        if (!status.equalsIgnoreCase("APPROVED") && !status.equalsIgnoreCase("REJECTED")) {
            throw new IllegalArgumentException("Invalid status value. Allowed values: APPROVED, REJECTED");
        }

        enrollment.setStatus(status.toUpperCase());
        enrollment.setModifiedAt(LocalDateTime.now());

        return enrollmentRepository.save(enrollment);
    }

    public void softDeleteEnrollment(UUID enrollmentId) {
        ProjectEnrollment enrollment = getEnrollmentById(enrollmentId);
        enrollment.setDeletedAt(LocalDateTime.now());
        enrollmentRepository.save(enrollment);
    }
}
