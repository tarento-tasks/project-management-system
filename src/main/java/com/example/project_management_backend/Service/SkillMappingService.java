package com.example.project_management_backend.Service;

import com.example.project_management_backend.DTO.SkillMappingRequest;
import com.example.project_management_backend.Model.SkillMapping;
import com.example.project_management_backend.Model.SkillMappingId;
import com.example.project_management_backend.Repository.SkillMappingRepository;
import com.example.project_management_backend.Repository.SkillRepository;
import com.example.project_management_backend.Repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class SkillMappingService {

    private final SkillMappingRepository skillMappingRepository;
    private final UserRepository userRepository;
    private final SkillRepository skillRepository;

    public SkillMappingService(SkillMappingRepository skillMappingRepository, UserRepository userRepository, SkillRepository skillRepository) {
        this.skillMappingRepository = skillMappingRepository;
        this.userRepository = userRepository;
        this.skillRepository = skillRepository;
    }

    public SkillMapping addSkillToUser(UUID userId, UUID skillId) {
        SkillMapping skillMapping = SkillMapping.builder()
                .id(new SkillMappingId(userId, skillId))
                .user(userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found")))
                .skill(skillRepository.findById(skillId).orElseThrow(() -> new RuntimeException("Skill not found")))
                .build();
        return skillMappingRepository.save(skillMapping);
    }

    @Transactional
    public List<SkillMappingRequest> getSkillMappings(UUID userId, UUID skillId) {
        List<SkillMapping> skillMappings;

        if (userId != null) {
            skillMappings = skillMappingRepository.findByUser_UserId(userId);
        } else if (skillId != null) {
            skillMappings = skillMappingRepository.findBySkill_SkillId(skillId);
        } else {
            skillMappings = skillMappingRepository.findAll();
        }

        return skillMappings.stream()
                .map(mapping -> new SkillMappingRequest(mapping.getUser().getUserId(), mapping.getSkill().getSkillId()))
                .collect(Collectors.toList());
    }
}
