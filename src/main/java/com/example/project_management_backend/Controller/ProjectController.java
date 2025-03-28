package com.example.project_management_backend.Controller;

import com.example.project_management_backend.DTO.ApiResponse;
import com.example.project_management_backend.DTO.ProjectDTO;
import com.example.project_management_backend.Service.ProjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.CrossOrigin;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@CrossOrigin(origins = "http://localhost:5173")
@RequestMapping("/api/projects")
public class ProjectController {

    @Autowired
    private ProjectService projectService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<ProjectDTO>>> getProjects(@RequestParam(required = false) UUID id) {
        return ResponseEntity.ok(projectService.getProjects(Optional.ofNullable(id)));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<ProjectDTO>> createOrUpdateProject(
            @RequestBody ProjectDTO projectDTO,
            @RequestParam(required = false) UUID id) {
        return ResponseEntity.ok(projectService.saveOrUpdateProject(Optional.ofNullable(id), projectDTO));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Object>> deleteProject(@PathVariable UUID id) {
        return ResponseEntity.ok(projectService.deleteProject(id));
    }
}
