package com.example.project_management_backend.Controller;

import com.example.project_management_backend.DTO.ApiResponse;
import com.example.project_management_backend.DTO.TaskDTO;
import com.example.project_management_backend.Service.TaskService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

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

    @PostMapping(consumes = "multipart/form-data")
    @PreAuthorize("hasAnyRole('MENTOR', 'ADMIN')")
    public ResponseEntity<ApiResponse<TaskDTO>> createOrUpdateTask(
        @RequestParam(value = "taskId", required = false) UUID taskId,  
        @RequestParam("taskName") String taskName,
        @RequestParam("projectId") UUID projectId,
        @RequestParam(value = "attachments", required = false) MultipartFile attachments,
        @RequestParam(value = "dueDate", required = false) LocalDateTime dueDate,
        @RequestParam(value = "studentStatus", required = false) String studentStatus,
        @RequestParam(value = "completeStatus", required = false) String completeStatus,
        @RequestParam(value = "openStatus", required = false) String openStatus,
        @RequestParam(value = "taskObjective", required = false) String taskObjective,
        @RequestParam(value = "modifiedBy", required = false) UUID modifiedBy
    ) {
        TaskDTO taskDTO = new TaskDTO();
        taskDTO.setTaskId(taskId);  
        taskDTO.setTaskName(taskName);
        taskDTO.setProjectId(projectId);
        taskDTO.setDueDate(dueDate);
        taskDTO.setStudentStatus(studentStatus);
        taskDTO.setCompleteStatus(completeStatus);
        taskDTO.setOpenStatus(openStatus);
        taskDTO.setTaskObjective(taskObjective);
        taskDTO.setModifiedBy(modifiedBy);

      
        if (attachments != null && !attachments.isEmpty()) {
            try {
                taskDTO.setAttachments(attachments.getBytes());
            } catch (IOException e) {
                return ResponseEntity.badRequest().body(
                    new ApiResponse<>(HttpStatus.BAD_REQUEST.value(), "File upload failed", null)
                );
            }
        }

        TaskDTO savedTask = taskService.createOrUpdateTask(taskDTO);
        return ResponseEntity.ok(
            new ApiResponse<>(HttpStatus.OK.value(), "Task created/updated successfully", savedTask)
        );
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('MENTOR', 'ADMIN', 'STUDENT')")
    public ResponseEntity<ApiResponse<?>> getTasks(
        @RequestParam(value = "projectId", required = false) UUID projectId,
        @RequestParam(value = "taskId", required = false) UUID taskId
    ) {
        if (projectId != null) {
            
            List<TaskDTO> tasks = taskService.getTasksByProject(projectId);
            return ResponseEntity.ok(
                new ApiResponse<>(HttpStatus.OK.value(), "Tasks fetched successfully", tasks)
            );
        } else if (taskId != null) {
           
            TaskDTO task = taskService.getTaskById(taskId);
            if (task != null) {
                return ResponseEntity.ok(
                    new ApiResponse<>(HttpStatus.OK.value(), "Task fetched successfully", task)
                );
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    new ApiResponse<>(HttpStatus.NOT_FOUND.value(), "Task not found", null)
                );
            }
        } else {
            
            if (SecurityContextHolder.getContext().getAuthentication().getAuthorities().stream()
                .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ROLE_ADMIN"))) {
                List<TaskDTO> tasks = taskService.getAllTasks();
                return ResponseEntity.ok(
                    new ApiResponse<>(HttpStatus.OK.value(), "All tasks fetched successfully", tasks)
                );
            } else {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(
                    new ApiResponse<>(HttpStatus.FORBIDDEN.value(), "Access denied", null)
                );
            }
        }
    }

    @DeleteMapping("/{taskId}")
    @PreAuthorize("hasAnyRole('MENTOR', 'ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteTask(@PathVariable UUID taskId) {
        boolean isDeleted = taskService.deleteTask(taskId);
        if (isDeleted) {
            return ResponseEntity.ok(
                new ApiResponse<>(HttpStatus.OK.value(), "Task deleted successfully", null)
            );
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                new ApiResponse<>(HttpStatus.NOT_FOUND.value(), "Task not found", null)
            );
        }
    }
}