package com.example.project_management_backend.Controller;

import com.example.project_management_backend.DTO.TaskDTO;
import com.example.project_management_backend.Service.TaskService;
import com.example.project_management_backend.Exception.ResourceNotFoundException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.http.HttpStatus;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {

    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    // ✅ **Create or update a task**
    @PostMapping("/save")
    public ResponseEntity<TaskDTO> createOrUpdateTask(@RequestBody TaskDTO taskDTO) {
        try {
            TaskDTO savedTask = taskService.createOrUpdateTask(taskDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedTask);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    // ✅ **Unified GET API for all task queries**
    @GetMapping
    public ResponseEntity<?> getTasks(
            @RequestParam(required = false) UUID taskId,
            @RequestParam(required = false) UUID projectId) {
        try {
            if (taskId != null) {
                // Fetch a single task by ID
                return ResponseEntity.ok(taskService.getTaskById(taskId));
            } else if (projectId != null) {
                // Fetch tasks by project ID
                return ResponseEntity.ok(taskService.getTasksByProject(projectId));
            } else {
                // Fetch all tasks
                return ResponseEntity.ok(taskService.getAllTasks());
            }
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Resource not found");
        }
    }

    @PutMapping("/update/{taskId}")
public ResponseEntity<TaskDTO> updateStudentTask(
        @PathVariable UUID taskId,
        @RequestParam(required = false) String studentStatus,
        @RequestParam(required = false) String taskName,
        @RequestParam(required = false) LocalDateTime dueDate,
        @RequestParam(required = false) String completeStatus,
        @RequestParam(required = false) String openStatus,
        @RequestParam(required = false) String taskObjective,
        @RequestParam(required = false) MultipartFile attachments) throws IOException{
    
        byte[] attachmentBytes = null;
        
        // Convert MultipartFile to byte[] if a file is provided
        if (attachments != null && !attachments.isEmpty()) {
            attachmentBytes = attachments.getBytes();
        }

        // Call service method to update task
        TaskDTO updatedTask = taskService.updateTask(taskId, studentStatus, taskName, dueDate, completeStatus, openStatus, taskObjective, attachmentBytes);

        // Return the updated task
        return ResponseEntity.ok(updatedTask);

}


    // ✅ **Soft delete a task**
    @DeleteMapping("/{taskId}")
    public ResponseEntity<String> deleteTask(@PathVariable UUID taskId) {
        boolean deleted = taskService.deleteTask(taskId);
        if (deleted) {
            return ResponseEntity.ok("Task deleted successfully.");
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Task already deleted or does not exist.");
        }
    }
}
