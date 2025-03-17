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

 
    public SkillDTO createSkill(SkillDTO skillDTO) {
        if (skillRepository.existsBySkillNameIgnoreCase(skillDTO.getSkillName())) {
            throw new RuntimeException("Skill already exists with name: " + skillDTO.getSkillName());
        }

        Skill skill = new Skill();
        skill.setSkillName(skillDTO.getSkillName());
        Skill savedSkill = skillRepository.save(skill);
        return new SkillDTO(savedSkill.getSkillId(), savedSkill.getSkillName());
    }


    public SkillDTO updateSkill(UUID id, SkillDTO skillDTO) {
        Optional<Skill> skillOpt = skillRepository.findById(id);
        if (skillOpt.isPresent()) {
            Skill existingSkill = skillOpt.get();

            if (!existingSkill.getSkillName().equalsIgnoreCase(skillDTO.getSkillName()) &&
                skillRepository.existsBySkillNameIgnoreCase(skillDTO.getSkillName())) {
                throw new RuntimeException("Skill name already exists: " + skillDTO.getSkillName());
            }

            existingSkill.setSkillName(skillDTO.getSkillName());
            Skill updatedSkill = skillRepository.save(existingSkill);
            return new SkillDTO(updatedSkill.getSkillId(), updatedSkill.getSkillName());
        }
        return null;
    }

   
    public SkillDTO getSkillById(UUID id) {
        Optional<Skill> skill = skillRepository.findById(id);
        return skill.map(s -> new SkillDTO(s.getSkillId(), s.getSkillName())).orElse(null);
    }

    public List<SkillDTO> getAllSkills() {
        return skillRepository.findAll()
                .stream()
                .map(skill -> new SkillDTO(skill.getSkillId(), skill.getSkillName()))
                .collect(Collectors.toList());
    }

    public void deleteSkill(UUID id) {
        skillRepository.deleteById(id);
    }
}
