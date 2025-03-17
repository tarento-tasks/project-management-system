package com.example.project_management_backend.Controller;

import com.example.project_management_backend.DTO.ProjectDTO;
import com.example.project_management_backend.Service.ProjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/projects")
public class ProjectController {

    @Autowired
    private ProjectService projectService;

    
    @GetMapping
    @PreAuthorize("hasAnyRole('MENTOR', 'ADMIN','STUDENT')")
    public ResponseEntity<List<ProjectDTO>> getProjects(@RequestParam(required = false) UUID id) {
    return ResponseEntity.ok(projectService.getProjects(Optional.ofNullable(id)));



}


    
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ProjectDTO> createOrUpdateProject(@RequestBody ProjectDTO projectDTO,
                                                            @RequestParam(required = false) UUID id) {
        try {
            ProjectDTO savedProject = projectService.saveOrUpdateProject(Optional.ofNullable(id), projectDTO);
            return ResponseEntity.ok(savedProject);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> deleteProject(@PathVariable UUID id) {
        if (projectService.deleteProject(id)) {
            return ResponseEntity.ok("Project deleted successfully");
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Project not found or already deleted");
    }
}