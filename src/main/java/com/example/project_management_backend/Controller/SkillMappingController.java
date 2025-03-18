package com.example.project_management_backend.Controller;

import com.example.project_management_backend.DTO.ApiResponse;
import com.example.project_management_backend.DTO.SkillMappingRequest;
import com.example.project_management_backend.Model.SkillMapping;
import com.example.project_management_backend.Service.SkillMappingService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/skill-mapping")
public class SkillMappingController {

    private final SkillMappingService skillMappingService;

    public SkillMappingController(SkillMappingService skillMappingService) {
        this.skillMappingService = skillMappingService;
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('MENTOR', 'ADMIN', 'STUDENT')")
    public ResponseEntity<ApiResponse<List<SkillMappingRequest>>> getSkillMappings(
            @RequestParam Optional<UUID> userId, 
            @RequestParam Optional<UUID> skillId) {

        List<SkillMappingRequest> skillMappings = skillMappingService.getSkillMappings(userId, skillId);
        ApiResponse<List<SkillMappingRequest>> response = new ApiResponse<>(200, "Skill mappings retrieved successfully", skillMappings);
        return ResponseEntity.ok(response);
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<SkillMappingRequest>> addOrUpdateSkillToUser(@RequestBody SkillMappingRequest request) {
        SkillMapping skillMapping = skillMappingService.addOrUpdateSkillToUser(request.getUserId(), request.getSkillId());
        SkillMappingRequest responseData = new SkillMappingRequest(skillMapping.getUser().getUserId(), skillMapping.getSkill().getSkillId());
        ApiResponse<SkillMappingRequest> response = new ApiResponse<>(200, "Skill mapping processed successfully", responseData);
        return ResponseEntity.ok(response);
    }


    @DeleteMapping("/{userId}/{skillId}")
@PreAuthorize("hasAnyRole('ADMIN', 'MENTOR', 'STUDENT')")
public ResponseEntity<ApiResponse<Void>> deleteSkillMapping(
        @PathVariable UUID userId, 
        @PathVariable UUID skillId) {
    
    skillMappingService.deleteSkillMapping(userId, skillId);
    ApiResponse<Void> response = new ApiResponse<>(200, "Skill mapping deleted successfully", null);
    return ResponseEntity.ok(response);
}

}
