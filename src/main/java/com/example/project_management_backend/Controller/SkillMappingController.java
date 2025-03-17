package com.example.project_management_backend.Controller;

import com.example.project_management_backend.DTO.SkillMappingRequest;
import com.example.project_management_backend.Model.SkillMapping;
import com.example.project_management_backend.Service.SkillMappingService;
import org.springframework.http.ResponseEntity;
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

    // 🔹 Unified GET: Fetch mappings by userId or skillId (or all if none provided)
    @GetMapping
    public ResponseEntity<List<SkillMappingRequest>> getSkillMappings(
            @RequestParam Optional<UUID> userId, 
            @RequestParam Optional<UUID> skillId) {
        
        return ResponseEntity.ok(skillMappingService.getSkillMappings(userId, skillId));
    }

    // 🔹 Single POST API: Add Skill Mapping (Ensuring User & Skill Exist)
    @PostMapping
    public ResponseEntity<SkillMappingRequest> addSkillToUser(@RequestBody SkillMappingRequest request) {
        SkillMapping skillMapping = skillMappingService.addSkillToUser(request.getUserId(), request.getSkillId());
        SkillMappingRequest response = new SkillMappingRequest(skillMapping.getUser().getUserId(), skillMapping.getSkill().getSkillId());
        return ResponseEntity.ok(response);
    }
}
