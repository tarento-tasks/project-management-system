package com.example.project_management_backend.Service;

import com.example.project_management_backend.DTO.TaskDTO;
import com.example.project_management_backend.Model.Task;
import com.example.project_management_backend.Repository.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class TaskService {

    private final TaskRepository taskRepository;

    @Autowired
    public TaskService(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    public TaskDTO createTask(TaskDTO taskDTO) {
        Task task = new Task();
        task.setTaskName(taskDTO.getTaskName());
        task.setAttachments(taskDTO.getAttachments());
        task.setDueDate(taskDTO.getDueDate());
        task.setStudentStatus(taskDTO.getStudentStatus());
        task.setCompleteStatus(taskDTO.getCompleteStatus());
        task.setOpenStatus(taskDTO.getOpenStatus());
        task.setTaskObjective(taskDTO.getTaskObjective());
        task.setCreatedAt(LocalDateTime.now()); 

        task = taskRepository.save(task);
        return convertToDTO(task);
    }

    public List<TaskDTO> getAllTasks() {
        return taskRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public TaskDTO getTaskById(UUID taskId) {
        return taskRepository.findById(taskId)
                .map(this::convertToDTO)
                .orElse(null);
    }

  
    public TaskDTO updateTask(UUID taskId, TaskDTO updatedTaskDTO) {
        Optional<Task> taskOptional = taskRepository.findById(taskId);
        if (taskOptional.isPresent()) {
            Task task = taskOptional.get();
            task.setTaskName(updatedTaskDTO.getTaskName());
            task.setAttachments(updatedTaskDTO.getAttachments());
            task.setDueDate(updatedTaskDTO.getDueDate());
            task.setStudentStatus(updatedTaskDTO.getStudentStatus());
            task.setCompleteStatus(updatedTaskDTO.getCompleteStatus());
            task.setOpenStatus(updatedTaskDTO.getOpenStatus());
            task.setTaskObjective(updatedTaskDTO.getTaskObjective());
            task.setModifiedAt(LocalDateTime.now()); // Set modified time

            taskRepository.save(task);
            return convertToDTO(task);
        }
        return null;
    }


    public boolean deleteTask(UUID taskId) {
        if (taskRepository.existsById(taskId)) {
            taskRepository.deleteById(taskId);
            return true;
        }
        return false;
    }

    private TaskDTO convertToDTO(Task task) {
        TaskDTO dto = new TaskDTO();
        dto.setTaskId(task.getTaskId());
        dto.setTaskName(task.getTaskName());
        dto.setAttachments(task.getAttachments());
        dto.setCreatedAt(task.getCreatedAt());
        dto.setDueDate(task.getDueDate());
        dto.setStudentStatus(task.getStudentStatus());
        dto.setCompleteStatus(task.getCompleteStatus());
        dto.setModifiedAt(task.getModifiedAt());
        dto.setOpenStatus(task.getOpenStatus());
        dto.setDeletedAt(task.getDeletedAt());
        dto.setTaskObjective(task.getTaskObjective());
        return dto;
    }
}
