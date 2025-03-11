package com.example.project_management_backend.Controller;

import com.example.project_management_backend.DTO.ProjectUpdatesDTO;
import com.example.project_management_backend.Service.ProjectUpdatesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/project-updates")
public class ProjectUpdatesController {

    @Autowired
    private ProjectUpdatesService projectUpdatesService;

    @GetMapping
    public ResponseEntity<List<ProjectUpdatesDTO>> getAllUpdates() {
        return ResponseEntity.ok(projectUpdatesService.getAllProjectUpdates());
    }

    @GetMapping("/{projectId}")
    public ResponseEntity<List<ProjectUpdatesDTO>> getUpdatesByProject(@PathVariable UUID projectId) {
        return ResponseEntity.ok(projectUpdatesService.getProjectUpdatesByProjectId(projectId));
    }

    @PostMapping(consumes = "multipart/form-data") // ✅ Accept multipart data
    public ResponseEntity<ProjectUpdatesDTO> createUpdate(
            @RequestParam UUID userId,
            @RequestParam UUID projectId,
            @RequestParam String updateText,
            @RequestParam(required = false) MultipartFile updateImage) {

        try {
            ProjectUpdatesDTO createdUpdate = projectUpdatesService.createProjectUpdate(userId, projectId, updateText, updateImage);
            return ResponseEntity.ok(createdUpdate);
        } catch (IOException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }
}
