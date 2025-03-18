package com.example.project_management_backend.Service;

import com.example.project_management_backend.Model.SkillMapping;
import com.example.project_management_backend.Model.SkillMappingId;
import com.example.project_management_backend.DTO.SkillMappingRequest;
import com.example.project_management_backend.Model.User;
import com.example.project_management_backend.Model.Skill;
import com.example.project_management_backend.Repository.SkillMappingRepository;
import com.example.project_management_backend.Repository.SkillRepository;
import com.example.project_management_backend.Repository.UserRepository;

import jakarta.transaction.Transactional;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;
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

    @Transactional
    public SkillMapping addOrUpdateSkillToUser(UUID userId, UUID skillId) {
        User user = userRepository.findById(userId)
                .filter(u -> u.getDeletedAt() == null)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "User not found or is deleted"));
        
        Skill skill = skillRepository.findById(skillId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Skill not found"));
        
        SkillMappingId id = new SkillMappingId(userId, skillId);
        
        Optional<SkillMapping> existingMapping = skillMappingRepository.findById(id);
        
        if (existingMapping.isPresent()) {
            return existingMapping.get();  
        }


        SkillMapping skillMapping = SkillMapping.builder()
                .id(id)
                .user(user)
                .skill(skill)
                .build();

        return skillMappingRepository.save(skillMapping);
    }

    @Transactional
    public List<SkillMappingRequest> getSkillMappings(Optional<UUID> userId, Optional<UUID> skillId) {
        if (userId.isPresent()) {
            return skillMappingRepository.findByUser_UserId(userId.get()).stream()
                    .map(mapping -> new SkillMappingRequest(mapping.getUser().getUserId(), mapping.getSkill().getSkillId()))
                    .collect(Collectors.toList());
        } else if (skillId.isPresent()) {
            return skillMappingRepository.findBySkill_SkillId(skillId.get()).stream()
                    .map(mapping -> new SkillMappingRequest(mapping.getUser().getUserId(), mapping.getSkill().getSkillId()))
                    .collect(Collectors.toList());
        }
        return skillMappingRepository.findAll().stream()
                .map(mapping -> new SkillMappingRequest(mapping.getUser().getUserId(), mapping.getSkill().getSkillId()))
                .collect(Collectors.toList());
    }

   
    @Transactional
public void deleteSkillMapping(UUID userId, UUID skillId) {
    SkillMappingId id = new SkillMappingId(userId, skillId);
    
    if (!skillMappingRepository.existsById(id)) {
        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Skill mapping not found");
    }
    
    skillMappingRepository.deleteById(id);
}

}
