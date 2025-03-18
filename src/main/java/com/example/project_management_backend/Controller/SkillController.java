package com.example.project_management_backend.Controller;

import com.example.project_management_backend.DTO.SkillDTO;
import com.example.project_management_backend.Service.SkillService;
import com.example.project_management_backend.DTO.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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
    @PreAuthorize("hasAnyRole('MENTOR', 'ADMIN', 'STUDENT')")
    public ResponseEntity<ApiResponse<?>> getSkills(
            @RequestParam(value = "id", required = false) UUID id,
            @RequestParam(value = "name", required = false) String name) {
        
  
        if (id != null) {
            SkillDTO skill = skillService.getSkillById(id);
            if (skill != null) {
                return ResponseEntity.ok(new ApiResponse<>(HttpStatus.OK.value(), "Skill found", skill));
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ApiResponse<>(HttpStatus.NOT_FOUND.value(), "Skill not found", null));
            }
        }

      
        List<SkillDTO> skills = skillService.getAllSkills();
        return ResponseEntity.ok(new ApiResponse<>(HttpStatus.OK.value(), "List of skills", skills));
    }

   
@PostMapping
@PreAuthorize("hasRole('ADMIN')")
public ResponseEntity<ApiResponse<SkillDTO>> createOrUpdateSkill(
        @RequestParam(value = "id", required = false) UUID id,
        @RequestBody SkillDTO skillDTO) {

    try {
        SkillDTO updatedSkill;
        if (id != null) {
            updatedSkill = skillService.updateSkill(id, skillDTO);
            if (updatedSkill != null) {
                return ResponseEntity.ok(new ApiResponse<>(HttpStatus.OK.value(), "Skill updated successfully", updatedSkill));
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ApiResponse<>(HttpStatus.NOT_FOUND.value(), "Skill not found", null));
            }
        }

        updatedSkill = skillService.createSkill(skillDTO);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse<>(HttpStatus.CREATED.value(), "Skill created successfully", updatedSkill));

    } catch (RuntimeException e) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(new ApiResponse<>(HttpStatus.CONFLICT.value(), e.getMessage(), null));
    }
}


    
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<String>> deleteSkill(@PathVariable UUID id) {
        skillService.deleteSkill(id);
        return ResponseEntity.ok(new ApiResponse<>(HttpStatus.OK.value(), "Skill deleted successfully", "Deleted"));
    }
}
