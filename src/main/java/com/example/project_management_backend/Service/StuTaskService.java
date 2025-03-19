package com.example.project_management_backend.Service;

import com.example.project_management_backend.DTO.StuTaskDTO;
import com.example.project_management_backend.Exception.AlreadyExistsException;
import com.example.project_management_backend.Exception.ResourceNotFoundException;
import com.example.project_management_backend.Model.StuTask;
import com.example.project_management_backend.Model.StuTaskId;
import com.example.project_management_backend.Model.Task;
import com.example.project_management_backend.Model.User;
import com.example.project_management_backend.Repository.StuTaskRepository;
import com.example.project_management_backend.Repository.TaskRepository;
import com.example.project_management_backend.Repository.UserRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StuTaskService {

    private final StuTaskRepository stuTaskRepository;
    private final UserRepository userRepository;
    private final TaskRepository taskRepository;

    @Transactional
    public StuTaskDTO assignTaskToStudent(UUID studentId, UUID taskId) {
        // Validate student exists and is not soft deleted
        User student = userRepository.findById(studentId)
                .filter(s -> s.getDeletedAt() == null)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found or has been deleted"));

        // Validate task exists and is not soft deleted
        Task task = taskRepository.findByTaskIdAndDeletedAtIsNull(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found or has been deleted"));

        // Check if assignment already exists
        StuTaskId stuTaskId = new StuTaskId(studentId, taskId);
        if (stuTaskRepository.existsById(stuTaskId)) {
            throw new AlreadyExistsException("Student is already assigned to this task");
        }

        // Create and save assignment
        StuTask stuTask = new StuTask(stuTaskId, student, task);
        stuTaskRepository.save(stuTask);

        return new StuTaskDTO(studentId, taskId);
    }

    public List<StuTaskDTO> getStudentsByTaskId(UUID taskId) {
        // Validate task exists and is not soft deleted
        Task task = taskRepository.findById(taskId)
                .filter(t -> t.getDeletedAt() == null)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found or has been deleted"));

        List<StuTask> stuTasks = stuTaskRepository.findByTaskTaskId(taskId);
        return stuTasks.stream().map(stuTask -> new StuTaskDTO(
                stuTask.getStudent().getUserId(),
                stuTask.getTask().getTaskId()
        )).collect(Collectors.toList());
    }

    public List<StuTaskDTO> getTasksByStudentId(UUID studentId) {
        // Validate student exists and is not soft deleted
        User student = userRepository.findById(studentId)
                .filter(s -> s.getDeletedAt() == null)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found or has been deleted"));

        List<StuTask> stuTasks = stuTaskRepository.findByStudentUserId(studentId);
        return stuTasks.stream().map(stuTask -> new StuTaskDTO(
                stuTask.getStudent().getUserId(),
                stuTask.getTask().getTaskId()
        )).collect(Collectors.toList());
    }
}
