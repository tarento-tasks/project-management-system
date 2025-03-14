package com.example.project_management_backend.Controller;
import com.example.project_management_backend.DTO.StuTaskDTO;
import com.example.project_management_backend.Service.StuTaskService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/stu-task")
@RequiredArgsConstructor
public class StuTaskController {

    private final StuTaskService stuTaskService;

    @PostMapping
    @PreAuthorize("hasAnyRole('MENTOR', 'ADMIN')")
    public ResponseEntity<StuTaskDTO> assignTaskToStudent(@RequestBody StuTaskDTO dto) {
        return ResponseEntity.ok(stuTaskService.addStuTask(dto));
    }

    @GetMapping("/{taskId}")
    @PreAuthorize("hasAnyRole('MENTOR', 'ADMIN','STUDENT')")
    public ResponseEntity<List<StuTaskDTO>> getStudentsByTaskId(@PathVariable UUID taskId) {
        return ResponseEntity.ok(stuTaskService.getStudentsByTaskId(taskId));
    }
}

