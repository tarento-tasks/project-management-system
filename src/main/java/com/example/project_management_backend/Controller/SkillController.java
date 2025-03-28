package com.example.project_management_backend.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import com.example.project_management_backend.DTO.SkillDTO;
import com.example.project_management_backend.Service.SkillService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@CrossOrigin(origins = "http://localhost:5173")
@RequestMapping("/api/skills")
public class SkillController {

    @Autowired
    private SkillService skillService;

    /**
     * Get all skills or a specific skill by ID
     */
    @GetMapping
    public ResponseEntity<?> getSkills(@RequestParam Optional<UUID> id) {
        if (id.isPresent()) {
            SkillDTO skill = skillService.getSkillById(id.get());
            return ResponseEntity.ok(skill);
        }
        List<SkillDTO> skills = skillService.getAllSkills();
        return ResponseEntity.ok(skills);
    }

    /**
     * Create or update a skill
     */
    @PostMapping
    public ResponseEntity<SkillDTO> createOrUpdateSkill(@RequestBody SkillDTO skillDTO,
                                                        @RequestParam(required = false) UUID id) {
        return ResponseEntity.ok(skillService.createOrUpdateSkill(id, skillDTO));
    }

    /**
     * Delete a skill
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteSkill(@PathVariable UUID id) {
        skillService.deleteSkill(id);
        return ResponseEntity.ok("Skill deleted successfully");
    }
}
