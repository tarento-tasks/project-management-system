package com.example.project_management_backend.Controller;

import com.example.project_management_backend.DTO.SkillDTO;
import com.example.project_management_backend.Service.SkillService;
import com.example.project_management_backend.Repository.SkillMappingRepository;
import com.example.project_management_backend.Model.SkillMapping;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;
import java.util.List;

@RestController
@RequestMapping("/api/skills")
public class SkillController {

    @Autowired
    private SkillService skillService;

    @Autowired
    private SkillMappingRepository skillMappingRepository;

    @GetMapping
    public List<SkillDTO> getAllSkills() {
        return skillService.getAllSkills();
    }

    @GetMapping("/{id}")
    public ResponseEntity<SkillDTO> getSkillById(@PathVariable UUID id) {
        SkillDTO skill = skillService.getSkillById(id);
        return (skill != null) ? ResponseEntity.ok(skill) : ResponseEntity.notFound().build();
    }

    @PostMapping
    public SkillDTO createSkill(@RequestBody SkillDTO skillDTO) {
        return skillService.createSkill(skillDTO);
    }

    @PutMapping("/{id}")
    public ResponseEntity<SkillDTO> updateSkill(@PathVariable UUID id, @RequestBody SkillDTO skillDTO) {
        SkillDTO updatedSkill = skillService.updateSkill(id, skillDTO);
        return (updatedSkill != null) ? ResponseEntity.ok(updatedSkill) : ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSkill(@PathVariable UUID id) {
        skillService.deleteSkill(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/mapping/{userId}")
    public ResponseEntity<List<SkillMapping>> getSkillsByUserId(@PathVariable UUID userId) {
        return ResponseEntity.ok(skillMappingRepository.findByUserId(userId));
    }
}
