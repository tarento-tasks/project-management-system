package com.example.project_management_backend.Controller;

import com.example.project_management_backend.DTO.TaskDTO;
import com.example.project_management_backend.Service.TaskService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {

    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

   
    @PostMapping
    public ResponseEntity<TaskDTO> createTask(@RequestBody TaskDTO taskDTO) {
        TaskDTO createdTask = taskService.createTask(taskDTO);
        return ResponseEntity.ok(createdTask);
    }

  
    @GetMapping
    public ResponseEntity<List<TaskDTO>> getAllTasks() {
        List<TaskDTO> tasks = taskService.getAllTasks();
        return ResponseEntity.ok(tasks);
    }

    
    @GetMapping("/{taskId}")
    public ResponseEntity<TaskDTO> getTaskById(@PathVariable UUID taskId) {
        TaskDTO task = taskService.getTaskById(taskId);
        return (task != null) ? ResponseEntity.ok(task) : ResponseEntity.notFound().build();
    }

    @PutMapping("/{taskId}")
    public ResponseEntity<TaskDTO> updateTask(@PathVariable UUID taskId, @RequestBody TaskDTO taskDTO) {
        TaskDTO updatedTask = taskService.updateTask(taskId, taskDTO);
        return (updatedTask != null) ? ResponseEntity.ok(updatedTask) : ResponseEntity.notFound().build();
    }

   
    @DeleteMapping("/{taskId}")
    public ResponseEntity<Void> deleteTask(@PathVariable UUID taskId) {
        boolean deleted = taskService.deleteTask(taskId);
        return deleted ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }
}

