package com.example.project_management_backend.Controller;
import com.example.project_management_backend.DTO.SkillDTO;
import com.example.project_management_backend.Service.SkillService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/skills")
public class SkillController {

    @Autowired
    private SkillService skillService;

   
    @GetMapping
    @PreAuthorize("hasAnyRole('MENTOR', 'ADMIN','STUDENT')")
    public List<SkillDTO> getAllSkills() {
        return skillService.getAllSkills();
    }

    
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('MENTOR', 'ADMIN','STUDENT')")
    public ResponseEntity<SkillDTO> getSkillById(@PathVariable("id") UUID id) {
       SkillDTO skill = skillService.getSkillById(id);
       return (skill != null) ? ResponseEntity.ok(skill) : ResponseEntity.notFound().build();
    }

    
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')") 
    public SkillDTO createSkill(@RequestBody SkillDTO skillDTO) {
        return skillService.createSkill(skillDTO);
    }

   
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')") 
    public ResponseEntity<SkillDTO> updateSkill(@PathVariable UUID id, @RequestBody SkillDTO skillDTO) {
        SkillDTO updatedSkill = skillService.updateSkill(id, skillDTO);
        return (updatedSkill != null) ? ResponseEntity.ok(updatedSkill) : ResponseEntity.notFound().build();
    }


    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')") 
    public ResponseEntity<String> deleteSkill(@PathVariable UUID id) {
        skillService.deleteSkill(id);
        return ResponseEntity.ok("deleted successfully");
    }
}
