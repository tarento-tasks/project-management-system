package com.example.project_management_backend.Service;

import com.example.project_management_backend.DTO.ApiResponse;
import com.example.project_management_backend.DTO.ProjectDTO;
import com.example.project_management_backend.DTO.ProjectSkillMappingDTO;
import com.example.project_management_backend.Model.*;
import com.example.project_management_backend.Repository.*;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class ProjectSkillMappingService {
    
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProjectSkillMappingRepository projectSkillMappingRepository;

    @Autowired
    private ProjectRepository projectRepository;
    
    
    @Autowired
    private SkillRepository skillRepository;

    @Autowired
    private SkillMappingRepository skillMappingRepository;

    @Autowired
    private ProjectEnrollmentRepository projectEnrollmentRepository;

    public ApiResponse<ProjectSkillMappingDTO> addSkillToProject(UUID projectId, UUID skillId) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Project not found"));
        Skill skill = skillRepository.findById(skillId)
                .orElseThrow(() -> new RuntimeException("Skill not found"));

        ProjectSkillMapping projectSkillMapping = ProjectSkillMapping.builder()
                .id(new ProjectSkillMappingId(projectId, skillId))
                .project(project)
                .skill(skill)
                .build();

        projectSkillMappingRepository.save(projectSkillMapping);
        ProjectSkillMappingDTO responseDTO = new ProjectSkillMappingDTO(projectId, skillId);

        return new ApiResponse<>(HttpStatus.CREATED.value(), "Skill mapped to project successfully", responseDTO);
    }

    public ApiResponse<?> getProjectSkillMappings(UUID studentId, UUID projectId, UUID skillId) {
        if (studentId != null) {
            return getRecommendedProjectsForStudent(studentId);
        } else if (projectId != null) {
            return getSkillsForProject(projectId);
        } else if (skillId != null) {
            return getProjectsForSkill(skillId);
        } else {
            return new ApiResponse<>(HttpStatus.BAD_REQUEST.value(), "Invalid request. Provide studentId, projectId, or skillId.", null);
        }
    }

    public ApiResponse<List<ProjectDTO>> getRecommendedProjectsForStudent(UUID studentId) {
        List<SkillMapping> studentSkills = skillMappingRepository.findByUser_UserId(studentId);
        List<UUID> skillIds = studentSkills.stream()
                .map(skill -> skill.getSkill().getSkillId())
                .collect(Collectors.toList());

        if (skillIds.isEmpty()) {
            return new ApiResponse<>(HttpStatus.OK.value(), "No skills found for student. No recommendations available.", Collections.emptyList());
        }

        List<ProjectSkillMapping> matchedProjects = projectSkillMappingRepository.findBySkill_SkillIdIn(skillIds);

        List<ProjectDTO> projectDTOs = matchedProjects.stream()
                .map(ProjectSkillMapping::getProject)
                .distinct()
                .map(project -> {
                    ProjectDTO dto = new ProjectDTO();
                    dto.setProjectId(project.getProjectId());
                    dto.setTitle(project.getTitle());
                    dto.setObjective(project.getObjective());
                    dto.setDescription(project.getDescription());
                    dto.setDueDate(project.getDueDate());
                    dto.setCriteria(project.getCriteria());
                    dto.setRepo(project.getRepo());
                    dto.setLastDate(project.getLastDate());
                    dto.setOpenStatus(project.isOpenStatus());
                    dto.setMentorId(project.getMentor().getUserId());
                    return dto;
                })
                .collect(Collectors.toList());

        return new ApiResponse<>(HttpStatus.OK.value(), "Recommended projects", projectDTOs);
    }
    public ApiResponse<List<User>> getRecommendedStudentsForProject(UUID projectId) {
        // Fetch required skills for the project
        List<UUID> requiredSkillIds = projectSkillMappingRepository.findByProject_ProjectId(projectId)
                .stream()
                .map(mapping -> mapping.getSkill().getSkillId())
                .collect(Collectors.toList());

        if (requiredSkillIds.isEmpty()) {
            return new ApiResponse<>(HttpStatus.OK.value(), "No skills required for this project", List.of());
        }

        // Fetch students who have sent an enrollment request for the project
        List<ProjectEnrollment> enrollments = projectEnrollmentRepository.findByProject_ProjectIdAndStatus(projectId, "PENDING");

        // Extract students from enrollments
        List<User> students = enrollments.stream()
                .map(ProjectEnrollment::getStudent)
                .collect(Collectors.toList());

        // Calculate compatibility and sort students
        List<User> recommendedStudents = students.stream()
                .filter(student -> student.getDeletedAt() == null) // Filter out deleted students
                .map(student -> {
                    // Fetch student's skills
                    List<UUID> studentSkillIds = skillMappingRepository.findByUser_UserId(student.getUserId())
                            .stream()
                            .map(mapping -> mapping.getSkill().getSkillId())
                            .collect(Collectors.toList());

                    // Calculate number of matching skills
                    long matchCount = studentSkillIds.stream()
                            .filter(requiredSkillIds::contains)
                            .count();

                    // Create a pair of student and match count for sorting
                    return new AbstractMap.SimpleEntry<>(student, matchCount);
                })
                .filter(entry -> entry.getValue() > 0) // Filter students with at least one matching skill
                .sorted((e1, e2) -> Long.compare(e2.getValue(), e1.getValue())) // Sort by match count (descending)
                .limit(10) // Limit to top 10 students
                .map(AbstractMap.SimpleEntry::getKey) // Extract the student from the pair
                .collect(Collectors.toList());

        return new ApiResponse<>(HttpStatus.OK.value(), "Recommended students fetched successfully", recommendedStudents);
    }
    private ApiResponse<List<ProjectSkillMappingDTO>> getSkillsForProject(UUID projectId) {
        List<ProjectSkillMappingDTO> mappings = projectSkillMappingRepository.findByProject_ProjectId(projectId)
                .stream()
                .map(mapping -> new ProjectSkillMappingDTO(mapping.getProject().getProjectId(), mapping.getSkill().getSkillId()))
                .collect(Collectors.toList());

        return new ApiResponse<>(HttpStatus.OK.value(), "Skills required for project", mappings);
    }

    private ApiResponse<List<ProjectSkillMappingDTO>> getProjectsForSkill(UUID skillId) {
        List<ProjectSkillMappingDTO> mappings = projectSkillMappingRepository.findBySkill_SkillId(skillId)
                .stream()
                .map(mapping -> new ProjectSkillMappingDTO(mapping.getProject().getProjectId(), mapping.getSkill().getSkillId()))
                .collect(Collectors.toList());

        return new ApiResponse<>(HttpStatus.OK.value(), "Projects requiring this skill", mappings);
    }
}
