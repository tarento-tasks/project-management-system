package com.example.project_management_backend.Service;

import com.example.project_management_backend.Model.Skill;
import com.example.project_management_backend.Model.SkillMapping;
import com.example.project_management_backend.Model.User;
import com.example.project_management_backend.Repository.SkillMappingRepository;
import com.example.project_management_backend.Repository.SkillRepository;
import com.example.project_management_backend.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class SkillMappingService {

    @Autowired
    private SkillMappingRepository skillMappingRepository;

    @Autowired
    private SkillRepository skillRepository;

    @Autowired
    private UserRepository userRepository;

    public List<SkillMapping> getAllSkillMappings() {
        return skillMappingRepository.findAll();
    }

    public List<SkillMapping> getSkillsByUserId(UUID userId) {
        return skillMappingRepository.findByUserId(userId);
    }

    public SkillMapping addSkillToUser(UUID userId, UUID skillId) {
        Optional<User> userOpt = userRepository.findById(userId);
        Optional<Skill> skillOpt = skillRepository.findById(skillId);

        if (userOpt.isPresent() && skillOpt.isPresent()) {
            SkillMapping skillMapping = new SkillMapping();
            skillMapping.setUser(userOpt.get());
            skillMapping.setSkill(skillOpt.get());
            return skillMappingRepository.save(skillMapping);
        }
        return null;
    }

    public void removeSkillFromUser(UUID mappingId) {
        skillMappingRepository.deleteById(mappingId);
    }
}
