package com.example.project_management_backend.Controller;
import com.example.project_management_backend.Model.Task;
import org.springframework.http.MediaType;
import com.example.project_management_backend.DTO.TaskDTO;
import com.example.project_management_backend.Service.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.http.HttpHeaders;
import java.io.IOException; // Correct import
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.UUID;

import com.example.project_management_backend.DTO.ApiResponse;

@RestController
@CrossOrigin(origins = "http://localhost:5173")
@RequestMapping("/api/tasks")
public class TaskController {

    private final TaskService taskService;

    @Autowired
    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    
    @PostMapping
    @PreAuthorize("hasRole('ADMIN') or @taskService.isAssignedMentor(#taskDTO.projectId)")
    public ResponseEntity<ApiResponse<TaskDTO>> createTask(@RequestBody TaskDTO taskDTO) {
        TaskDTO createdTask = taskService.createTask(taskDTO);
        ApiResponse<TaskDTO> response = new ApiResponse<>(200, "Task created successfully", createdTask);
        return ResponseEntity.ok(response);
    }
    @GetMapping("/{taskId}/attachment")
    public ResponseEntity<byte[]> downloadAttachment(@PathVariable UUID taskId) {
        Task task = taskService.getTaskById(taskId);
        if (task.getAttachments() == null || task.getAttachments().length == 0) {
            return ResponseEntity.notFound().build();
        }
 
        // Determine content type (you might want to store this when uploading)
        String contentType = "application/octet-stream"; // Default to binary
 
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"task_attachment_" + taskId + "\"")
                .body(task.getAttachments());
    }
 
 

    @PutMapping("/{taskId}")
    @PreAuthorize("hasRole('ADMIN') or @taskService.isAssignedMentor(#taskId) or @taskService.isAssignedStudent(#taskId)")
    public ResponseEntity<ApiResponse<TaskDTO>> updateTask(
            @PathVariable UUID taskId,
            @RequestParam(value = "taskName", required = false) String taskName,
            @RequestParam(value = "taskObjective", required = false) String taskObjective,
            @RequestParam(value = "dueDate", required = false) String dueDateStr,
            @RequestParam(value = "completeStatus", required = false) String completeStatus,
            @RequestParam(value = "studentStatus", required = false) String studentStatus,
            @RequestParam(value = "attachments", required = false) MultipartFile attachments) {
 
        // First get the existing task to preserve existing values
        TaskDTO existingTaskDTO = taskService.convertToDTO(taskService.getTaskById(taskId));
 
        // Only update fields that were provided
        if (taskName != null) {
            existingTaskDTO.setTaskName(taskName);
        }
        if (taskObjective != null) {
            existingTaskDTO.setTaskObjective(taskObjective);
        }
        if (dueDateStr != null) {
            try {
                LocalDateTime dueDate = LocalDateTime.parse(dueDateStr, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
                existingTaskDTO.setDueDate(dueDate);
            } catch (DateTimeParseException e) {
                throw new RuntimeException("Invalid date format. Expected format: yyyy-MM-dd'T'HH:mm:ss", e);
            }
        }
        if (completeStatus != null) {
            existingTaskDTO.setCompleteStatus(completeStatus);
        }
        if (studentStatus != null) {
            existingTaskDTO.setStudentStatus(studentStatus);
        }
        if (attachments != null) {
            try {
                existingTaskDTO.setAttachments(attachments.getBytes());
            } catch (IOException e) {
                throw new RuntimeException("Failed to process file upload", e);
            }
        }
 
        TaskDTO updatedTask = taskService.updateTask(taskId, existingTaskDTO);
        ApiResponse<TaskDTO> response = new ApiResponse<>(200, "Task updated successfully", updatedTask);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping
   
    public ResponseEntity<ApiResponse<List<TaskDTO>>> getTasks(
            @RequestParam(required = false) UUID projectId) {

        List<TaskDTO> tasks;

        if (projectId == null) {
            // Fetch all tasks
            tasks = taskService.getAllTasks();
        } else {
            // Fetch tasks by projectId
            tasks = taskService.getTasksByProjectId(projectId);
        }

        ApiResponse<List<TaskDTO>> response = new ApiResponse<>(200, "Tasks retrieved successfully", tasks);
        return ResponseEntity.ok(response);
    }


   
    @DeleteMapping("/{taskId}")
    @PreAuthorize("hasRole('ADMIN') or @taskService.isAssignedMentor(#taskId)")
    public ResponseEntity<ApiResponse<Void>> deleteTask(@PathVariable UUID taskId) {
        taskService.deleteTask(taskId);
        ApiResponse<Void> response = new ApiResponse<>(200, "Task deleted successfully", null);
        return ResponseEntity.ok(response);
    }
}