package com.example.project_management_backend.Controller;
import com.example.project_management_backend.DTO.StuTaskDTO;
import com.example.project_management_backend.Service.StuTaskService;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/stu-task")
@RequiredArgsConstructor
public class StuTaskController {

    private final StuTaskService stuTaskService;

    @PostMapping("/students/{studentId}/tasks/{taskId}")
    public ResponseEntity<StuTaskDTO> assignTaskToStudent(
            @PathVariable UUID studentId, 
            @PathVariable UUID taskId) {
        
        StuTaskDTO assignedTask = stuTaskService.assignTaskToStudent(studentId, taskId);
        return ResponseEntity.status(HttpStatus.CREATED).body(assignedTask);
    }


    @GetMapping("/{taskId}")
    public ResponseEntity<List<StuTaskDTO>> getStudentsByTaskId(@PathVariable UUID taskId) {
        return ResponseEntity.ok(stuTaskService.getStudentsByTaskId(taskId));
    }
}

