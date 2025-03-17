package com.example.project_management_backend.Controller;

import com.example.project_management_backend.DTO.SkillMappingRequest;
import com.example.project_management_backend.Model.SkillMapping;
import com.example.project_management_backend.Service.SkillMappingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/skill-mapping")
public class SkillMappingController {

    @Autowired
    private SkillMappingService skillMappingService;

   
    @GetMapping("/user/{userId}")
    @PreAuthorize("hasAnyRole('MENTOR', 'ADMIN','STUDENT')")
    public ResponseEntity<List<SkillMappingRequest>> getSkillMappingsByUserId(@PathVariable UUID userId) {
        return ResponseEntity.ok(skillMappingService.getSkillMappingsByUserId(userId));
    }

    @GetMapping("/skill/{skillId}")
    @PreAuthorize("hasAnyRole('MENTOR', 'ADMIN','STUDENT')")
    public ResponseEntity<List<SkillMappingRequest>> getSkillMappingsBySkillId(@PathVariable UUID skillId) {
        return ResponseEntity.ok(skillMappingService.getSkillMappingsBySkillId(skillId));
    }
   
    @PostMapping
    @PreAuthorize("hasAnyRole('MENTOR', 'ADMIN','STUDENT')")
    public ResponseEntity<SkillMappingRequest> addSkillToUser(@RequestBody SkillMappingRequest request) {
    SkillMapping skillMapping = skillMappingService.addSkillToUser(request.getUserId(), request.getSkillId());
    
    SkillMappingRequest response = new SkillMappingRequest(
        skillMapping.getId().getUserId(),
        skillMapping.getId().getSkillId()
    );
    
    return ResponseEntity.status(HttpStatus.CREATED).body(response);
}
    
}
