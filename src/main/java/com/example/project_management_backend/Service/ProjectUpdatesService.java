package com.example.project_management_backend.Service;

import com.example.project_management_backend.DTO.ProjectUpdatesDTO;
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

    // ✅ Fetch all updates
    public List<ProjectUpdatesDTO> getAllProjectUpdates() {
        List<ProjectUpdates> updates = projectUpdatesRepository.findAll();
        return updates.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    // ✅ Fetch updates by projectId
    @Transactional
    public List<ProjectUpdatesDTO> getProjectUpdatesByProjectId(UUID projectId) {
        List<ProjectUpdates> updates = projectUpdatesRepository.findByProjectId_ProjectId(projectId);
        return updates.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    // ✅ Create a project update (Ensure projectId exists)
    public ProjectUpdatesDTO createProjectUpdate( UUID userId,UUID projectId, String updateText, MultipartFile updateImage) throws IOException {
        Optional<Project> projectOptional = projectRepository.findById(projectId);
        
        // ✅ Ensure project exists and is not soft deleted
        if (projectOptional.isEmpty() || projectOptional.get().getDeletedAt() != null) {
            throw new RuntimeException("Project not found or deleted!");
        }

        ProjectUpdates updates = new ProjectUpdates();
        updates.setUserId(userId);
        updates.setProjectId(projectOptional.get()); // ✅ Use renamed field
        updates.setUpdateText(updateText);

        if (updateImage != null && !updateImage.isEmpty()) {
            updates.setUpdateImage(updateImage.getBytes()); // ✅ Convert MultipartFile to byte[]
        }

        updates.setCreatedAt(LocalDateTime.now());

        ProjectUpdates savedUpdates = projectUpdatesRepository.save(updates);
        return convertToDTO(savedUpdates);
    }

    // ✅ Convert Entity to DTO
    private ProjectUpdatesDTO convertToDTO(ProjectUpdates updates) {
        return new ProjectUpdatesDTO(
                updates.getUpdateId(),
                updates.getUserId(),
                updates.getProjectId().getProjectId(), // ✅ Extract UUID from Project entity
                updates.getUpdateText(),
                updates.getCreatedAt()
        );
    }
}
