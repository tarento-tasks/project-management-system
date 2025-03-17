package com.example.project_management_backend.Controller;

import com.example.project_management_backend.DTO.ApiResponse;
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

    @GetMapping
    @PreAuthorize("hasAnyRole('MENTOR', 'ADMIN','STUDENT')")
    public ResponseEntity<ApiResponse<List<SkillMappingRequest>>> getSkillMappings(
            @RequestParam(value = "userId", required = false) UUID userId,
            @RequestParam(value = "skillId", required = false) UUID skillId) {

        List<SkillMappingRequest> skillMappings = skillMappingService.getSkillMappings(userId, skillId);
        return ResponseEntity.ok(new ApiResponse<>(HttpStatus.OK.value(), "Skill mappings fetched successfully", skillMappings));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('MENTOR', 'ADMIN','STUDENT')")
    public ResponseEntity<ApiResponse<SkillMappingRequest>> addSkillToUser(@RequestBody SkillMappingRequest request) {
        SkillMapping skillMapping = skillMappingService.addSkillToUser(request.getUserId(), request.getSkillId());
        SkillMappingRequest response = new SkillMappingRequest(skillMapping.getId().getUserId(), skillMapping.getId().getSkillId());
        
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse<>(HttpStatus.CREATED.value(), "Skill added to user successfully", response));
    }
}
