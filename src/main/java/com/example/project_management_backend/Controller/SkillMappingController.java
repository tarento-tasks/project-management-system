package com.example.project_management_backend.Controller;
import com.example.project_management_backend.Model.SkillMapping;
import com.example.project_management_backend.Service.SkillMappingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/skill-mapping")
public class SkillMappingController {

    @Autowired
    private SkillMappingService skillMappingService;

    @GetMapping
    public List<SkillMapping> getAllSkillMappings() {
        return skillMappingService.getAllSkillMappings();
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<SkillMapping>> getSkillsByUserId(@PathVariable UUID userId) {
        return ResponseEntity.ok(skillMappingService.getSkillsByUserId(userId));
    }

    @PostMapping("/add")
    public ResponseEntity<SkillMapping> addSkillToUser(@RequestParam UUID userId, @RequestParam UUID skillId) {
        SkillMapping skillMapping = skillMappingService.addSkillToUser(userId, skillId);
        return (skillMapping != null) ? ResponseEntity.ok(skillMapping) : ResponseEntity.badRequest().build();
    }

    @DeleteMapping("/{mappingId}")
    public ResponseEntity<Void> removeSkillFromUser(@PathVariable UUID mappingId) {
        skillMappingService.removeSkillFromUser(mappingId);
        return ResponseEntity.noContent().build();
    }
}
