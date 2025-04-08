package com.example.project_management_backend.Controller;
import com.example.project_management_backend.Model.User;
import com.example.project_management_backend.DTO.ApiResponse;
import com.example.project_management_backend.DTO.ProjectDTO;
import com.example.project_management_backend.DTO.ProjectSkillMappingDTO;
import com.example.project_management_backend.Service.ProjectSkillMappingService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
 

@RestController
@CrossOrigin(origins = "http://localhost:5173")
@RequestMapping("/api/project-skills")
public class ProjectSkillMappingController {

    @Autowired
    private ProjectSkillMappingService projectSkillMappingService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<ProjectSkillMappingDTO>> addSkillToProject(
            @RequestBody ProjectSkillMappingDTO request) {
        return ResponseEntity.ok(projectSkillMappingService.addSkillToProject(request.getProjectId(), request.getSkillId()));
    }

    @GetMapping("/project/{projectId}")
    @PreAuthorize("hasAnyRole('STUDENT', 'MENTOR', 'ADMIN')")
    public ResponseEntity<ApiResponse<?>> getSkillsByProjectId(@PathVariable UUID projectId) {
        return ResponseEntity.ok(projectSkillMappingService.getSkillsByProjectId(projectId));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('STUDENT', 'MENTOR', 'ADMIN')")
    public ResponseEntity<ApiResponse<?>> getProjectSkillMappings(
            @RequestParam(required = false) UUID studentId,
            @RequestParam(required = false) UUID projectId,
            @RequestParam(required = false) UUID skillId) {
        return ResponseEntity.ok(projectSkillMappingService.getProjectSkillMappings(studentId, projectId, skillId));
    }

    @GetMapping("/recommendations/projects/{studentId}")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<ApiResponse<List<ProjectDTO>>> getRecommendedProjects(@PathVariable UUID studentId) {
        return ResponseEntity.ok(projectSkillMappingService.getRecommendedProjectsForStudent(studentId));
    }

    @GetMapping("/recommendations/students/{projectId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<User>>> getRecommendedStudentsForProject(@PathVariable UUID projectId) {
        return ResponseEntity.ok(projectSkillMappingService.getRecommendedStudentsForProject(projectId));
    }
}
