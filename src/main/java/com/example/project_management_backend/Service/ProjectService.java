package com.example.project_management_backend.Service;

import com.example.project_management_backend.DTO.ProjectDTO;
import com.example.project_management_backend.Exception.AlreadyExistsException;
import com.example.project_management_backend.Exception.BadRequestException;
import com.example.project_management_backend.Exception.ResourceNotFoundException;
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
                    .orElseThrow(() -> new ResourceNotFoundException("Project not found or has been deleted"));
        }
        return projectRepository.findByDeletedAtIsNull().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    
    @Transactional
    public ProjectDTO saveOrUpdateProject(Optional<UUID> projectId, ProjectDTO projectDTO) {
    if (projectDTO.getTitle() == null || projectDTO.getTitle().trim().isEmpty()) {
        throw new BadRequestException("Project title cannot be empty");
    }
    if (projectDTO.getRepo() == null || projectDTO.getRepo().trim().isEmpty()) {
        throw new BadRequestException("Repository link cannot be empty");
    }

    
    Project project = projectId.flatMap(id -> projectRepository.findById(id)
                    .filter(p -> p.getDeletedAt() == null))
            .orElse(null);

    if (projectId.isPresent() && project == null) {
        throw new ResourceNotFoundException("Project not found or is deleted");
    }

   
    if (project == null && projectRepository.existsByTitleAndRepoAndDeletedAtIsNull(projectDTO.getTitle(), projectDTO.getRepo())) {
        throw new AlreadyExistsException("A project with the same title and repository already exists");
    }

   
    if (project == null) {
        project = new Project();
    }

   
    User mentor = userRepository.findById(projectDTO.getMentorId())
            .orElseThrow(() -> new ResourceNotFoundException("Mentor not found"));

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
                }).orElseThrow(() -> new ResourceNotFoundException("Project not found or already deleted"));
    }
}
