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
    public List<ProjectDTO> getAllProjects() {
        List<Project> projects = projectRepository.findByDeletedAtIsNull();
        return projects.stream().map(this::convertToDTO).collect(Collectors.toList());
    }
    
    @Transactional
    public Optional<ProjectDTO> getProjectById(UUID id) {
        return projectRepository.findByProjectIdAndDeletedAtIsNull(id)
                .map(this::convertToDTO);
    }
    

   
    public ProjectDTO createProject(ProjectDTO projectDTO) {
        Optional<User> mentorOpt = userRepository.findById(projectDTO.getMentorId());
        if (mentorOpt.isEmpty()) {
            throw new IllegalArgumentException("Mentor not found");
        }

        if (!projectDTO.getLastDate().isBefore(projectDTO.getDueDate())) {    throw new IllegalArgumentException("Last date must be before due date");}

        Project project = new Project();
        project.setTitle(projectDTO.getTitle());
        project.setObjective(projectDTO.getObjective());
        project.setDescription(projectDTO.getDescription());
        project.setDueDate(projectDTO.getDueDate());
        project.setCriteria(projectDTO.getCriteria());
        project.setRepo(projectDTO.getRepo());
        project.setLastDate(projectDTO.getLastDate());
        project.setOpenStatus(projectDTO.isOpenStatus());
        project.setMentor(mentorOpt.get());

        Project savedProject = projectRepository.save(project);
        return convertToDTO(savedProject);
    }


    public Optional<ProjectDTO> updateProject(UUID id, ProjectDTO projectDTO) {
        Optional<Project> projectOpt = projectRepository.findById(id);
        if (projectOpt.isPresent()) {
            Project project = projectOpt.get();

            if (!projectDTO.getLastDate().isBefore(projectDTO.getDueDate())) {    throw new IllegalArgumentException("Last date must be before due date");}

            project.setTitle(projectDTO.getTitle());
            project.setObjective(projectDTO.getObjective());
            project.setDescription(projectDTO.getDescription());
            project.setDueDate(projectDTO.getDueDate());
            project.setCriteria(projectDTO.getCriteria());
            project.setRepo(projectDTO.getRepo());
            project.setLastDate(projectDTO.getLastDate());
            project.setOpenStatus(projectDTO.isOpenStatus());

            Project updatedProject = projectRepository.save(project);
            return Optional.of(convertToDTO(updatedProject));
        }
        return Optional.empty();
    }

    @Transactional
    public boolean deleteProject(UUID id) {
        Optional<Project> projectOpt = projectRepository.findById(id);
        if (projectOpt.isPresent()) {
            Project project = projectOpt.get();
            if (project.getDeletedAt() == null) {  
                project.setDeletedAt(LocalDateTime.now()); 
                projectRepository.save(project);
                return true;
            }
        }
        return false;
    }
}