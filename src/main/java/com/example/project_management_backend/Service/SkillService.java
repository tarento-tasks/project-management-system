package com.example.project_management_backend.Service;

import com.example.project_management_backend.DTO.SkillDTO;
import com.example.project_management_backend.Model.Skill;
import com.example.project_management_backend.Repository.SkillRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class SkillService {

    @Autowired
    private SkillRepository skillRepository;

   
    public List<SkillDTO> getAllSkills() {
        return skillRepository.findAll()
                .stream()
                .map(skill -> new SkillDTO(skill.getSkillId(), skill.getSkillName()))
                .collect(Collectors.toList());
    }

   
    public SkillDTO getSkillById(UUID id) {
        return skillRepository.findById(id)
                .map(skill -> new SkillDTO(skill.getSkillId(), skill.getSkillName()))
                .orElse(null);
    }

   
    public SkillDTO createOrUpdateSkill(UUID id, SkillDTO skillDTO) {
        Optional<Skill> existingSkill = skillRepository.findBySkillName(skillDTO.getSkillName());

        
        if (existingSkill.isPresent() && (id == null || !existingSkill.get().getSkillId().equals(id))) {
            return null;
        }

        Skill skill = (id == null) ? new Skill() : skillRepository.findById(id).orElse(new Skill());
        skill.setSkillName(skillDTO.getSkillName());
        Skill savedSkill = skillRepository.save(skill);
        return new SkillDTO(savedSkill.getSkillId(), savedSkill.getSkillName());
    }

    
    public void deleteSkill(UUID id) {
        skillRepository.deleteById(id);
    }
}
