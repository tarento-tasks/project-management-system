package com.example.project_management_backend.Service;

import com.example.project_management_backend.DTO.TaskDTO;
import com.example.project_management_backend.Model.Task;
import com.example.project_management_backend.Model.Project;
import com.example.project_management_backend.Model.User;
import com.example.project_management_backend.Repository.TaskRepository;
import com.example.project_management_backend.Repository.ProjectRepository;
import com.example.project_management_backend.Repository.UserRepository;
import com.example.project_management_backend.Repository.StuTaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import jakarta.transaction.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class TaskService {

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private StuTaskRepository stuTaskRepository;

    // Create a new task (only ADMIN or assigned mentor)
    @Transactional
    public TaskDTO createTask(TaskDTO taskDTO) {
        // Fetch the authenticated user
        User user = getAuthenticatedUser();

        // Fetch the project
        Project project = projectRepository.findById(taskDTO.getProjectId())
                .orElseThrow(() -> new RuntimeException("Project not found"));

        // Ensure the user is ADMIN or the assigned mentor
        if (!user.getRole().getRoleName().equals("ADMIN") && !user.getUserId().equals(project.getMentor().getUserId())) {
            throw new RuntimeException("Only ADMIN or assigned mentor can create tasks");
        }

        // Create a new task
        Task task = new Task();
        task.setTaskName(taskDTO.getTaskName());
        task.setTaskObjective(taskDTO.getTaskObjective());
        task.setDueDate(taskDTO.getDueDate());
        task.setCompleteStatus("Not Completed"); // Default status
        task.setProject(project);
        task.setCreatedAt(LocalDateTime.now());

        // Save the task
        Task savedTask = taskRepository.save(task);
        return convertToDTO(savedTask);
    }
    

    

    @Transactional
    public TaskDTO updateTask(UUID taskId, TaskDTO taskDTO) {
        // Fetch the authenticated user
        User user = getAuthenticatedUser();

        // Fetch the task
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Task not found"));

        // Fetch the project
        Project project = task.getProject();

        // Check if the user is ADMIN, assigned mentor, or assigned student
        boolean isAdmin = user.getRole().getRoleName().equals("ADMIN");
        boolean isAssignedMentor = user.getUserId().equals(project.getMentor().getUserId());
        boolean isAssignedStudent = isAssignedStudent(project.getProjectId());

        if (!isAdmin && !isAssignedMentor && !isAssignedStudent) {
            throw new RuntimeException("You are not authorized to update this task");
        }

        // Update fields based on user role
        if (isAdmin || isAssignedMentor) {
            // ADMIN or assigned mentor can update all fields except attachments and studentStatus
            task.setTaskName(taskDTO.getTaskName());
            task.setTaskObjective(taskDTO.getTaskObjective());
            task.setDueDate(taskDTO.getDueDate());
            task.setCompleteStatus(taskDTO.getCompleteStatus());
        } else if (isAssignedStudent) {
            // Assigned student can only update attachments and studentStatus
            task.setAttachments(taskDTO.getAttachments()); // BLOB field
            task.setStudentStatus(taskDTO.getStudentStatus());
        }

        task.setModifiedAt(LocalDateTime.now());
        task.setModifiedBy(user.getUserId());

        // Save the updated task
        Task updatedTask = taskRepository.save(task);
        return convertToDTO(updatedTask);
    }

    // Get all tasks for a project (ADMIN, assigned mentor, or assigned student)
    @Transactional
    public List<TaskDTO> getTasksByProjectId(UUID projectId) {
        // Fetch the authenticated user
        User user = getAuthenticatedUser();

        // Fetch the project
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Project not found"));

        // Check if the user is ADMIN, assigned mentor, or assigned student
        boolean isAdmin = user.getRole().getRoleName().equals("ADMIN");
        boolean isAssignedMentor = user.getUserId().equals(project.getMentor().getUserId());
        boolean isAssignedStudent = isAssignedStudent(project.getProjectId());

        if (!isAdmin && !isAssignedMentor && !isAssignedStudent) {
            throw new RuntimeException("You are not authorized to view tasks for this project");
        }

        // Fetch tasks for the project
        return taskRepository.findByProject_ProjectId(projectId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    // Delete a task (only ADMIN or assigned mentor)
    @Transactional
    public void deleteTask(UUID taskId) {
        // Fetch the authenticated user
        User user = getAuthenticatedUser();

        // Fetch the task
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Task not found"));

        // Fetch the project
        Project project = task.getProject();

        // Ensure the user is ADMIN or the assigned mentor
        if (!user.getRole().getRoleName().equals("ADMIN") && !user.getUserId().equals(project.getMentor().getUserId())) {
            throw new RuntimeException("Only ADMIN or assigned mentor can delete tasks");
        }

        // Soft delete the task
        task.setDeletedAt(LocalDateTime.now());
        taskRepository.save(task);
    }

    // Helper method to convert Task to TaskDTO
    private TaskDTO convertToDTO(Task task) {
        TaskDTO dto = new TaskDTO();
        dto.setTaskId(task.getTaskId());
        dto.setTaskName(task.getTaskName());
        dto.setTaskObjective(task.getTaskObjective());
        dto.setDueDate(task.getDueDate());
        dto.setCompleteStatus(task.getCompleteStatus());
        dto.setStudentStatus(task.getStudentStatus());
        dto.setAttachments(task.getAttachments()); // BLOB field
        dto.setProjectId(task.getProject().getProjectId());
        return dto;
    }

    // Helper method to get the authenticated user
    private User getAuthenticatedUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByEmailAndDeletedAtIsNull(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    // Helper method to check if the user is the assigned mentor
    public boolean isAssignedMentor(UUID projectId) {
        User user = getAuthenticatedUser();
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Project not found"));
        return user.getUserId().equals(project.getMentor().getUserId());
    }

    // Helper method to check if the user is an assigned student
    public boolean isAssignedStudent(UUID projectId) {
        // Fetch the authenticated user
        User user = getAuthenticatedUser();

        // Fetch all tasks for the project
        List<Task> tasks = taskRepository.findByProject_ProjectId(projectId);

        // Check if the student is assigned to any task in the project
        for (Task task : tasks) {
            boolean isAssigned = stuTaskRepository.existsById_StudentIdAndId_TaskIdAndDeletedAtIsNull(
                    user.getUserId(), task.getTaskId());
            if (isAssigned) {
                return true;
            }
        }

        return false;
    }
}