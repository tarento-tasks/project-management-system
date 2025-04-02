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
 
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
 
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
        
        if (project.getLastDate() != null && project.getLastDate().isBefore(LocalDate.now())) {
        throw new IllegalStateException("The last date to enroll in this project has passed.");
    }
       
         // Check if the student is already enrolled in the project
         boolean isAlreadyEnrolled = enrollmentRepository.existsByStudent_UserIdAndProject_ProjectIdAndDeletedAtIsNull(
            student.getUserId(), project.getProjectId());
 
     if (isAlreadyEnrolled) {
        throw new IllegalStateException("Student is already enrolled in this project.");
    }
 
       
        ProjectEnrollment enrollment = new ProjectEnrollment();
        enrollment.setStudent(student);
        enrollment.setProject(project);
        enrollment.setStatus("PENDING");
 
        return enrollmentRepository.save(enrollment);
    }
 
    public List<Project> getApprovedProjectsForStudent(UUID studentId) {
    // Verify student exists
    User student = userRepository.findById(studentId)
            .orElseThrow(() -> new RuntimeException("Student not found"));
    
    // Check if the user is actually a student
    if (!"STUDENT".equals(student.getRole().getRoleName())) {
        throw new AccessDeniedException("Only students can have project enrollments");
    }
    
    // Get all approved enrollments for this student
    List<ProjectEnrollment> enrollments = enrollmentRepository
            .findByStudent_UserIdAndStatusAndDeletedAtIsNullAndProject_DeletedAtIsNull(
                studentId,
                "APPROVED"
            );
    
    // Extract the projects from the enrollments
    return enrollments.stream()
            .map(ProjectEnrollment::getProject)
            .collect(Collectors.toList());
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
 