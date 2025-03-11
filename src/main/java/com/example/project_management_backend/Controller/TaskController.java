package com.example.project_management_backend.Controller;

import com.example.project_management_backend.DTO.TaskDTO;
import com.example.project_management_backend.Service.TaskService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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
    public ResponseEntity<TaskDTO> createTask(
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
                return ResponseEntity.badRequest().body(null);
            }
        }

        TaskDTO createdTask = taskService.createTask(taskDTO);
        return ResponseEntity.ok(createdTask);
    }

 
    @GetMapping("/project/{projectId}")
    @PreAuthorize("hasAnyRole('MENTOR', 'ADMIN','STUDENT')")

    public ResponseEntity<List<TaskDTO>> getTasksByProject(@PathVariable UUID projectId) {
        List<TaskDTO> tasks = taskService.getTasksByProject(projectId);
        return ResponseEntity.ok(tasks);
    }


    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<List<TaskDTO>> getAllTasks() {
        List<TaskDTO> tasks = taskService.getAllTasks();
        return ResponseEntity.ok(tasks);
    }


    @DeleteMapping("/{taskId}")
    @PreAuthorize("hasAnyRole('MENTOR', 'ADMIN')")
    public ResponseEntity<Void> deleteTask(@PathVariable UUID taskId) {
        boolean isDeleted = taskService.deleteTask(taskId);
        return isDeleted ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }



    @GetMapping("/{taskId}")
    @PreAuthorize("hasAnyRole('MENTOR', 'ADMIN','STUDENT')")
    public ResponseEntity<TaskDTO> getTaskById(@PathVariable UUID taskId) {
        TaskDTO task = taskService.getTaskById(taskId);
        return (task != null) ? ResponseEntity.ok(task) : ResponseEntity.notFound().build();
    }



    @PutMapping(value = "/{taskId}", consumes = "multipart/form-data")
    @PreAuthorize("hasAnyRole('MENTOR', 'ADMIN')")
    public ResponseEntity<TaskDTO> updateTask(
            @PathVariable UUID taskId,
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
                return ResponseEntity.badRequest().body(null);
            }
        }

        TaskDTO updatedTask = taskService.updateTask(taskDTO);
        return ResponseEntity.ok(updatedTask);
    }
}