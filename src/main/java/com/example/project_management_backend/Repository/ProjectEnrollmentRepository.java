package com.example.project_management_backend.Repository;
import com.example.project_management_backend.Model.ProjectEnrollment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ProjectEnrollmentRepository extends JpaRepository<ProjectEnrollment, UUID> {

    
    List<ProjectEnrollment> findByProject_DeletedAtIsNullAndDeletedAtIsNull();

    Optional<ProjectEnrollment> findByEnrollmentIdAndProject_DeletedAtIsNullAndDeletedAtIsNull(UUID enrollmentId);

    Optional<ProjectEnrollment> findByStudent_UserIdAndProject_ProjectIdAndDeletedAtIsNull(UUID studentId, UUID projectId);

    List<ProjectEnrollment> findByStudent_UserIdAndStatusAndDeletedAtIsNullAndProject_DeletedAtIsNull(
    UUID studentId, 
    String status);

    boolean existsByStudent_UserIdAndProject_ProjectIdAndDeletedAtIsNull(UUID studentId, UUID projectId);
}