package com.example.project_management_backend.Service;
import com.example.project_management_backend.DTO.StuTaskDTO;
import com.example.project_management_backend.Model.StuTask;
import com.example.project_management_backend.Model.StuTaskId;
import com.example.project_management_backend.Model.Task;
import com.example.project_management_backend.Model.User;
import com.example.project_management_backend.Repository.StuTaskRepository;
import com.example.project_management_backend.Repository.TaskRepository;
import com.example.project_management_backend.Repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StuTaskService {

    private final StuTaskRepository stuTaskRepository;
    private final UserRepository userRepository;
    private final TaskRepository taskRepository;

    // Add or update task assignment
    public StuTaskDTO addOrUpdateStuTask(StuTaskDTO dto) {
        User student = userRepository.findById(dto.getStudentId())
                .orElseThrow(() -> new EntityNotFoundException("Student not found"));

        Task task = taskRepository.findById(dto.getTaskId())
                .orElseThrow(() -> new EntityNotFoundException("Task not found"));

        StuTaskId stuTaskId = new StuTaskId(dto.getStudentId(), dto.getTaskId());
        StuTask stuTask = stuTaskRepository.findById(stuTaskId).orElse(null);

        if (stuTask != null) {
            // Update existing record
            if (stuTask.getDeletedAt() != null) {
                stuTask.setDeletedAt(null);  // Restore if it was soft deleted
            }
        } else {
            // Create new record
            stuTask = StuTask.builder()
                    .id(stuTaskId)
                    .student(student)
                    .task(task)
                    .build();
        }

        stuTaskRepository.save(stuTask);
        return dto;
    }

    // Get all students assigned to a task
    public List<StuTaskDTO> getStudentsByTaskId(UUID taskId) {
        List<StuTask> stuTasks = stuTaskRepository.findByTaskTaskId(taskId);
        return stuTasks.stream()
                .filter(stuTask -> stuTask.getDeletedAt() == null) // Exclude soft-deleted records
                .map(stuTask -> new StuTaskDTO(stuTask.getStudent().getUserId(), stuTask.getTask().getTaskId()))
                .collect(Collectors.toList());
    }

    // Soft delete a task assignment
    public void softDeleteStuTask(UUID studentId, UUID taskId) {
        StuTaskId id = new StuTaskId(studentId, taskId);
        StuTask stuTask = stuTaskRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Student-task assignment not found"));

        if (stuTask.getDeletedAt() != null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "This task assignment is already deleted");
        }

        stuTask.setDeletedAt(LocalDateTime.now());
        stuTaskRepository.save(stuTask);
    }
}