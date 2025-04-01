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
@CrossOrigin(origins = "http://localhost:5173")
@RequestMapping("/api/project-updates") 
public class ProjectUpdatesController {

    @Autowired
    private ProjectUpdatesService projectUpdatesService;

    @Autowired
    private JwtUtil jwtUtil;  

  
    @GetMapping
    public ResponseEntity<List<ProjectUpdatesDTO>> getAllUpdates() {
        return ResponseEntity.ok(projectUpdatesService.getAllProjectUpdates());
    }

   
    @GetMapping("/{projectId}")
    public ResponseEntity<List<ProjectUpdatesDTO>> getUpdatesByProject(@PathVariable UUID projectId) {
        return ResponseEntity.ok(projectUpdatesService.getProjectUpdatesByProjectId(projectId));
    }

    
    @PostMapping(consumes = "multipart/form-data")
    @PreAuthorize("hasAnyRole('ADMIN','MENTOR','STUDENT')")
    public ResponseEntity<ProjectUpdatesDTO> createUpdate(
          
            @RequestParam UUID projectId, 
            @RequestHeader("Authorization") String token,
            
            @RequestParam String updateText,
            @RequestParam(required = false) MultipartFile updateImage) {
        if (token == null || !token.startsWith("Bearer ")) {
            return ResponseEntity.status(401).body(null);
        }

        String jwt = token.substring(7);  

        try {
            UUID userId = jwtUtil.extractUserId(jwt); 

            ProjectUpdatesDTO createdUpdate = projectUpdatesService.createProjectUpdate(  userId, projectId, updateText,updateImage);
            return ResponseEntity.ok(createdUpdate);
        } catch (IOException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }
}