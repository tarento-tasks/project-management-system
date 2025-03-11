package com.example.project_management_backend.Service;

import com.example.project_management_backend.DTO.TaskDTO;
import com.example.project_management_backend.Model.Project;
import com.example.project_management_backend.Model.Task;
import com.example.project_management_backend.Repository.ProjectRepository;
import com.example.project_management_backend.Repository.TaskRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Optional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class TaskService {

    private final TaskRepository taskRepository;
    private final ProjectRepository projectRepository;

    @Autowired
    public TaskService(TaskRepository taskRepository, ProjectRepository projectRepository) {
        this.taskRepository = taskRepository;
        this.projectRepository = projectRepository;
    }

    
    public TaskDTO createTask(TaskDTO taskDTO) {
        Project project = projectRepository.findById(taskDTO.getProjectId())
                .orElseThrow(() -> new RuntimeException("Project not found"));

        Task task = new Task();
        task.setTaskName(taskDTO.getTaskName());
        task.setDueDate(taskDTO.getDueDate());
        task.setStudentStatus(taskDTO.getStudentStatus());
        task.setCompleteStatus(taskDTO.getCompleteStatus());
        task.setOpenStatus(taskDTO.getOpenStatus());
        task.setTaskObjective(taskDTO.getTaskObjective());
        task.setModifiedBy(taskDTO.getModifiedBy()); 
        task.setCreatedAt(LocalDateTime.now());
        task.setProject(project);

       
        task.setAttachments(taskDTO.getAttachments());

        task = taskRepository.save(task);
        return convertToDTO(task);
    }

   
    @Transactional
    public TaskDTO updateTask(TaskDTO taskDTO) {
        Task task = taskRepository.findById(taskDTO.getTaskId())
                .orElseThrow(() -> new RuntimeException("Task not found"));

        task.setTaskName(taskDTO.getTaskName());
        task.setDueDate(taskDTO.getDueDate());
        task.setStudentStatus(taskDTO.getStudentStatus());
        task.setCompleteStatus(taskDTO.getCompleteStatus());
        task.setOpenStatus(taskDTO.getOpenStatus());
        task.setTaskObjective(taskDTO.getTaskObjective());
        task.setModifiedBy(taskDTO.getModifiedBy());
        task.setModifiedAt(LocalDateTime.now());

        
        if (taskDTO.getAttachments() != null) {
            task.setAttachments(taskDTO.getAttachments());
        }

        task = taskRepository.save(task);
        return convertToDTO(task);
    }

    @Transactional
    
    public List<TaskDTO> getTasksByProject(UUID projectId) {
        return taskRepository.findByProject_ProjectId(projectId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    
    public TaskDTO getTaskById(UUID taskId) {
        return taskRepository.findById(taskId)
                .map(this::convertToDTO)
                .orElse(null);
    }

    
    public List<TaskDTO> getAllTasks() {
        return taskRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    
    @Transactional
    public boolean deleteTask(UUID taskId) {
        Optional<Task> taskOpt = taskRepository.findById(taskId);
        if (taskOpt.isPresent()) {
            Task task = taskOpt.get();
            if (task.getDeletedAt() == null) {  
                task.setDeletedAt(LocalDateTime.now()); 
                taskRepository.save(task);
                return true;
            }
        }
        return false;
    }


    
    private TaskDTO convertToDTO(Task task) {
        TaskDTO dto = new TaskDTO();
        dto.setTaskId(task.getTaskId());
        dto.setTaskName(task.getTaskName());
        dto.setCreatedAt(task.getCreatedAt());
        dto.setDueDate(task.getDueDate());
        dto.setStudentStatus(task.getStudentStatus());
        dto.setCompleteStatus(task.getCompleteStatus());
        dto.setModifiedAt(task.getModifiedAt());
        dto.setOpenStatus(task.getOpenStatus());
        dto.setDeletedAt(task.getDeletedAt());
        dto.setTaskObjective(task.getTaskObjective());
        dto.setModifiedBy(task.getModifiedBy());

        if (task.getProject() != null) {
            dto.setProjectId(task.getProject().getProjectId());
        }

       
        if (task.getAttachments() != null) {
            dto.setAttachments(task.getAttachments()); 
        }

        return dto;
    }
}