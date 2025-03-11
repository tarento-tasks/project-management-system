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
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StuTaskService {

    private final StuTaskRepository stuTaskRepository;
    private final UserRepository userRepository;
    private final TaskRepository taskRepository;

    // Add a Student-Task Mapping
    public StuTaskDTO addStuTask(StuTaskDTO dto) {
        User student = userRepository.findById(dto.getStudentId())
                .orElseThrow(() -> new EntityNotFoundException("Student not found"));

        Task task = taskRepository.findById(dto.getTaskId())
                .orElseThrow(() -> new EntityNotFoundException("Task not found"));

        StuTask stuTask = StuTask.builder()
                .id(new StuTaskId(dto.getStudentId(), dto.getTaskId()))
                .student(student)
                .task(task)
                .build();

        stuTaskRepository.save(stuTask);
        return dto;
    }

    // Get all students assigned to a specific task
    public List<StuTaskDTO> getStudentsByTaskId(UUID taskId) {
        List<StuTask> stuTasks = stuTaskRepository.findByTaskTaskId(taskId);
        return stuTasks.stream().map(stuTask -> {
            StuTaskDTO dto = new StuTaskDTO();
            dto.setStudentId(stuTask.getStudent().getUserId());
            dto.setTaskId(stuTask.getTask().getTaskId());
            return dto;
        }).collect(Collectors.toList());
    }
}

