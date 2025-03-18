package com.example.project_management_backend.Service;

import com.example.project_management_backend.DTO.ProjectDTO;
import com.example.project_management_backend.Model.Project;
import com.example.project_management_backend.Model.User;
import com.example.project_management_backend.Repository.ProjectRepository;
import com.example.project_management_backend.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import jakarta.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ProjectService {

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private UserRepository userRepository;

    private ProjectDTO convertToDTO(Project project) {
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
    }

   
    @Transactional
    public List<ProjectDTO> getProjects(Optional<UUID> projectId) {
        if (projectId.isPresent()) {
            return projectRepository.findByProjectIdAndDeletedAtIsNull(projectId.get())
                    .map(this::convertToDTO)
                    .map(List::of)
                    .orElseThrow(() -> new RuntimeException("Project not found"));
        }
        return projectRepository.findByDeletedAtIsNull().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

   
    @Transactional
    public ProjectDTO saveOrUpdateProject(Optional<UUID> projectId, ProjectDTO projectDTO) {

        
        if (projectRepository.findByTitleIgnoreCase(projectDTO.getTitle()).isPresent()) {
            throw new IllegalArgumentException("Project title already exists. Choose a different name.");
        }
        Project project = projectId.flatMap(projectRepository::findById)
                .filter(p -> p.getDeletedAt() == null)
                .orElse(new Project()); 

       
        User mentor = userRepository.findById(projectDTO.getMentorId())
                .orElseThrow(() -> new IllegalArgumentException("Mentor not found"));

        
                if (!projectDTO.getLastDate().isBefore(projectDTO.getDueDate())) {    throw new IllegalArgumentException("Last date must be before due date");}
        
                

        project.setTitle(projectDTO.getTitle());
        project.setObjective(projectDTO.getObjective());
        project.setDescription(projectDTO.getDescription());
        project.setDueDate(projectDTO.getDueDate());
        project.setCriteria(projectDTO.getCriteria());
        project.setRepo(projectDTO.getRepo());
        project.setLastDate(projectDTO.getLastDate());
        project.setOpenStatus(projectDTO.isOpenStatus());
        project.setMentor(mentor);

       
        return convertToDTO(projectRepository.save(project));
    }

    @Transactional
    public boolean deleteProject(UUID id) {
        return projectRepository.findById(id)
                .filter(p -> p.getDeletedAt() == null) 
                .map(p -> {
                    p.setDeletedAt(LocalDateTime.now());
                    projectRepository.save(p);
                    return true;
                }).orElse(false);
    }
}