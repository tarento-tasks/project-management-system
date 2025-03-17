package com.example.project_management_backend.Controller;

import com.example.project_management_backend.DTO.SkillDTO;
import com.example.project_management_backend.Service.SkillService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/skills")
public class SkillController {

    @Autowired
    private SkillService skillService;

    @GetMapping
    public ResponseEntity<?> getSkills(@RequestParam Optional<UUID> id) {
    if (id.isPresent()) {
        SkillDTO skill = skillService.getSkillById(id.get());
        return (skill != null) ? ResponseEntity.ok(skill) : ResponseEntity.notFound().build();
    }
    List<SkillDTO> skills = skillService.getAllSkills();
    return ResponseEntity.ok(skills);
}


    @PostMapping
    public ResponseEntity<?> createOrUpdateSkill(@RequestBody SkillDTO skillDTO, 
                                                 @RequestParam(required = false) UUID id) {
        SkillDTO skill = skillService.createOrUpdateSkill(id, skillDTO);
        return skill != null ? ResponseEntity.ok(skill) : ResponseEntity.badRequest().body("Skill already exists");
    }

    
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteSkill(@PathVariable UUID id) {
        skillService.deleteSkill(id);
        return ResponseEntity.ok("Skill deleted successfully");
    }
}
