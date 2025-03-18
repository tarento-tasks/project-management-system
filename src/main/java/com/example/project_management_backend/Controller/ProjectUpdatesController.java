package com.example.project_management_backend.Controller;

import com.example.project_management_backend.DTO.ProjectUpdatesDTO;
import com.example.project_management_backend.Service.ProjectUpdatesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.List;
import java.util.UUID;
import com.example.project_management_backend.config.JwtUtil;

@RestController
@RequestMapping("/api/project-updates") // ✅ Fixed base API path
public class ProjectUpdatesController {

    @Autowired
    private ProjectUpdatesService projectUpdatesService;

    @Autowired
    private JwtUtil jwtUtil;  // Inject JwtUtil

    // ✅ Fetch all updates
    @GetMapping
    public ResponseEntity<List<ProjectUpdatesDTO>> getAllUpdates() {
        return ResponseEntity.ok(projectUpdatesService.getAllProjectUpdates());
    }

    // ✅ Fetch updates by projectId
    @GetMapping("/{projectId}")
    public ResponseEntity<List<ProjectUpdatesDTO>> getUpdatesByProject(@PathVariable UUID projectId) {
        return ResponseEntity.ok(projectUpdatesService.getProjectUpdatesByProjectId(projectId));
    }

    // ✅ Create a project update (Accepts multipart data)
    @PostMapping(consumes = "multipart/form-data")
    @PreAuthorize("hasAnyRole('ADMIN','MENTOR','STUDENT')")
    public ResponseEntity<ProjectUpdatesDTO> createUpdate(
            // Extract token from header
            @RequestParam UUID projectId, // ✅ Changed to @RequestParam
            @RequestHeader("Authorization") String token,
            //@RequestParam UUID userId,
            @RequestParam String updateText,
            @RequestParam(required = false) MultipartFile updateImage) {
        if (token == null || !token.startsWith("Bearer ")) {
            return ResponseEntity.status(401).body(null);
        }

        String jwt = token.substring(7);  // Remove "Bearer " prefix

        try {
            UUID userId = jwtUtil.extractUserId(jwt); // Extract userId from token

            ProjectUpdatesDTO createdUpdate = projectUpdatesService.createProjectUpdate(  userId, projectId, updateText,updateImage);
            return ResponseEntity.ok(createdUpdate);
        } catch (IOException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }
}
