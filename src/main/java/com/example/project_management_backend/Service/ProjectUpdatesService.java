package com.example.project_management_backend.Service;

import com.example.project_management_backend.DTO.ProjectUpdatesDTO;

import com.example.project_management_backend.Exception.BadRequestException;
import com.example.project_management_backend.Exception.ResourceNotFoundException;
import com.example.project_management_backend.Model.Project;
import com.example.project_management_backend.Model.ProjectUpdates;
import com.example.project_management_backend.Repository.ProjectRepository;
import com.example.project_management_backend.Repository.ProjectUpdatesRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;



@Service
public class ProjectUpdatesService {

    @Autowired
    private ProjectUpdatesRepository projectUpdatesRepository;

    @Autowired
    private ProjectRepository projectRepository;


    public List<ProjectUpdatesDTO> getAllProjectUpdates() {
        List<ProjectUpdates> updates = projectUpdatesRepository.findAll();
        return updates.stream().map(this::convertToDTO).collect(Collectors.toList());
    }


    @Transactional
    public List<ProjectUpdatesDTO> getProjectUpdatesByProjectId(UUID projectId) {

        if (!projectRepository.existsById(projectId)) {
            throw new ResourceNotFoundException("Project not found with ID: " + projectId);
        }

        List<ProjectUpdates> updates = projectUpdatesRepository.findByProjectId_ProjectId(projectId);

        if (updates.isEmpty()) {
            throw new ResourceNotFoundException("No project updates found for project ID: " + projectId);
        }

        return updates.stream().map(this::convertToDTO).collect(Collectors.toList());
    }


    public ProjectUpdatesDTO createProjectUpdate( UUID userId,UUID projectId, String updateText, MultipartFile updateImage) throws IOException {
        Optional<Project> projectOptional = projectRepository.findById(projectId);



        if (projectOptional.isEmpty() || projectOptional.get().getDeletedAt() != null) {
            throw new ResourceNotFoundException("Project not found or has been deleted!");
        }



   
        if (updateText == null || updateText.trim().isEmpty()) {
            throw new BadRequestException("Update text cannot be empty.");
        }



        ProjectUpdates updates = new ProjectUpdates();
        updates.setUserId(userId);
        updates.setProjectId(projectOptional.get()); 
        updates.setUpdateText(updateText);

        if (updateImage != null && !updateImage.isEmpty()) {
            updates.setUpdateImage(updateImage.getBytes()); 
        }

        updates.setCreatedAt(LocalDateTime.now());

        ProjectUpdates savedUpdates = projectUpdatesRepository.save(updates);
        return convertToDTO(savedUpdates);
    }

   
    private ProjectUpdatesDTO convertToDTO(ProjectUpdates updates) {
        return new ProjectUpdatesDTO(
                updates.getUpdateId(),
                updates.getUserId(),
                updates.getProjectId().getProjectId(), 
                updates.getUpdateText(),
                updates.getCreatedAt()
        );
    }
}