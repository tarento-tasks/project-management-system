package com.example.project_management_backend.Controller;
import com.example.project_management_backend.DTO.StuTaskDTO;
import com.example.project_management_backend.Service.StuTaskService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

import com.example.project_management_backend.DTO.ApiResponse;



@RestController
@CrossOrigin(origins = "http://localhost:5173")

@RequestMapping("/api/stu-task")
@RequiredArgsConstructor
public class StuTaskController {

    private final StuTaskService stuTaskService;

    
    @PostMapping
    @PreAuthorize("hasAnyRole('MENTOR', 'ADMIN')")
    public ResponseEntity<ApiResponse<StuTaskDTO>> addOrUpdateStuTask(@RequestBody StuTaskDTO dto) {
        StuTaskDTO updatedTask = stuTaskService.addOrUpdateStuTask(dto);
        ApiResponse<StuTaskDTO> response = new ApiResponse<>(200, "Task assigned/updated successfully", updatedTask);
        return ResponseEntity.ok(response);
    }

 
    @GetMapping("/{taskId}")
    @PreAuthorize("hasAnyRole('MENTOR', 'ADMIN','STUDENT')")
    public ResponseEntity<ApiResponse<List<StuTaskDTO>>> getStudentsByTaskId(@PathVariable UUID taskId) {
        List<StuTaskDTO> students = stuTaskService.getStudentsByTaskId(taskId);
        ApiResponse<List<StuTaskDTO>> response = new ApiResponse<>(200, "Students retrieved successfully", students);
        return ResponseEntity.ok(response);
    }

 
    @DeleteMapping("/{studentId}/{taskId}")
    @PreAuthorize("hasAnyRole('MENTOR', 'ADMIN')")
    public ResponseEntity<ApiResponse<Void>> softDeleteStuTask(@PathVariable UUID studentId, @PathVariable UUID taskId) {
        stuTaskService.softDeleteStuTask(studentId, taskId);
        ApiResponse<Void> response = new ApiResponse<>(200, "Task assignment deleted successfully", null);
        return ResponseEntity.ok(response);
    }
}
