package com.example.project_management_backend.Service;

import com.example.project_management_backend.DTO.TaskDTO;
import com.example.project_management_backend.Exception.ResourceNotFoundException;
import com.example.project_management_backend.Exception.BadRequestException;
import com.example.project_management_backend.Exception.AlreadyExistsException;
import com.example.project_management_backend.Exception.AccessDeniedException;
import com.example.project_management_backend.Model.Project;
import com.example.project_management_backend.Model.Task;
import com.example.project_management_backend.Model.User;
import com.example.project_management_backend.Model.StuTask;
import com.example.project_management_backend.Repository.ProjectRepository;
import com.example.project_management_backend.Repository.TaskRepository;
import com.example.project_management_backend.Repository.StuTaskRepository;
import com.example.project_management_backend.Repository.UserRepository;
import com.example.project_management_backend.Service.UserService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class TaskService {

    private final TaskRepository taskRepository;
    private final ProjectRepository projectRepository;
    private final StuTaskRepository stuTaskRepository;
    private final UserRepository userRepository;

    @Autowired
    public TaskService(TaskRepository taskRepository, ProjectRepository projectRepository, 
                       StuTaskRepository stuTaskRepository, UserRepository userRepository) {
        this.taskRepository = taskRepository;
        this.projectRepository = projectRepository;
        this.stuTaskRepository = stuTaskRepository;
        this.userRepository = userRepository;
    }
    @Autowired
    private UserService UserService;

    @Transactional
    public TaskDTO createOrUpdateTask(TaskDTO taskDTO) {
        User currentUser = getCurrentUser();

        if (taskDTO.getTaskName() == null || taskDTO.getTaskName().trim().isEmpty()) {
            throw new BadRequestException("Task name cannot be empty");
        }

        Project project = projectRepository.findById(taskDTO.getProjectId())
                .orElseThrow(() -> new ResourceNotFoundException("Project not found"));

        // **Check if the user is an Admin or the Mentor assigned to this project**
        if (!currentUser.getRole().equals("ADMIN") && !project.getMentor().getUserId().equals(currentUser.getUserId())) {
            throw new AccessDeniedException("You are not authorized to create or update this task");
        }

        if (taskDTO.getTaskId() != null && !taskRepository.existsById(taskDTO.getTaskId())) {
            throw new ResourceNotFoundException("Task not found");
        }

        Task task = taskDTO.getTaskId() != null ?
                taskRepository.findById(taskDTO.getTaskId())
                        .orElseThrow(() -> new ResourceNotFoundException("Task not found"))
                : new Task();

        // **Prevent updating deleted tasks**
        if (task.getDeletedAt() != null) {
            throw new ResourceNotFoundException("Task has been deleted and cannot be updated");
        }

        // **Check for duplicate task name within the same project**
        if (taskDTO.getTaskId() == null) { // Only check for duplicates when creating a new task
            boolean exists = taskRepository.existsByTaskNameAndProject_ProjectId(taskDTO.getTaskName(), project.getProjectId());
            if (exists) {
                throw new AlreadyExistsException("A task with this name already exists in the project");
            }
        }

        task.setTaskName(taskDTO.getTaskName());
        task.setDueDate(taskDTO.getDueDate());
        task.setStudentStatus(taskDTO.getStudentStatus());
        task.setCompleteStatus(taskDTO.getCompleteStatus());
        task.setOpenStatus(taskDTO.getOpenStatus());
        task.setTaskObjective(taskDTO.getTaskObjective());
        task.setModifiedBy(taskDTO.getModifiedBy());
        task.setProject(project);

        if (taskDTO.getTaskId() == null) {
            task.setCreatedAt(LocalDateTime.now());
        } else {
            task.setModifiedAt(LocalDateTime.now());
        }

        if (taskDTO.getAttachments() != null) {
            task.setAttachments(taskDTO.getAttachments());
        }

        return convertToDTO(taskRepository.save(task));
    }

    @Transactional
    public TaskDTO updateTask(UUID taskId, String studentStatus, String taskName, LocalDateTime dueDate, String completeStatus, String openStatus, String taskObjective, byte[] attachments) {
        // Get the current user (admin, mentor, or student)
        User currentUser = UserService.getCurrentUser();

        // Fetch the task by ID
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found"));

        // Prevent updating deleted tasks
        if (task.getDeletedAt() != null) {
            throw new ResourceNotFoundException("Task has been deleted and cannot be modified");
        }

        // Check if the current user is allowed to update this task (admin or mentor)
        boolean isAssignedToStudent = stuTaskRepository.existsByStudent_UserIdAndTask_TaskId(currentUser.getUserId(), taskId);
        boolean isAdminOrMentor = currentUser.getRole().equals("ADMIN") || currentUser.getRole().equals("MENTOR");

        if (isAdminOrMentor) {
            // **Mentor/Project Validation:**
            if (currentUser.getRole().equals("MENTOR")) {
                // Check if mentor is assigned to the project of this task
                Project project = task.getProject(); // Get the project the task belongs to
                if (project.getMentor() == null || !project.getMentor().getUserId().equals(currentUser.getUserId())) {
                    throw new AccessDeniedException("You are not assigned to this project, so you cannot modify this task");
                }
            }

            // Admin or mentor: Allow all fields to be updated
            if (taskName != null) {
                task.setTaskName(taskName);
            }
            if (dueDate != null) {
                task.setDueDate(dueDate);
            }
            if (completeStatus != null) {
                task.setCompleteStatus(completeStatus);
            }
            if (openStatus != null) {
                task.setOpenStatus(openStatus);
            }
            if (taskObjective != null) {
                task.setTaskObjective(taskObjective);
            }
            if (attachments != null) {
                task.setAttachments(attachments);
            }
        } else if (isAssignedToStudent) {
            // Student: Only allow updates to studentStatus and attachments
            if (studentStatus != null) {
                task.setStudentStatus(studentStatus);
            }
            if (attachments != null) {
                task.setAttachments(attachments);
            }
        } else {
            // If neither admin/mentor nor student is assigned to the task
            throw new AccessDeniedException("You are not assigned to this task and cannot update it");
        }

        // Set modified timestamp and the user who modified the task
        task.setModifiedAt(LocalDateTime.now());
        task.setModifiedBy(currentUser.getUserId());

        // Save the updated task to the database
        return convertToDTO(taskRepository.save(task));
    }

    

    @Transactional
    public List<TaskDTO> getTasksByProject(UUID projectId) {
        if (!projectRepository.existsById(projectId)) {
            throw new ResourceNotFoundException("Project not found");
        }
        return taskRepository.findByProject_ProjectId(projectId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public TaskDTO getTaskById(UUID taskId) {
        return taskRepository.findById(taskId)
                .map(this::convertToDTO)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found"));
    }

    public List<TaskDTO> getAllTasks() {
        return taskRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public boolean deleteTask(UUID taskId) {
        User currentUser = getCurrentUser();
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found"));

        Project project = task.getProject();
        // **Check if user is an Admin or the assigned Mentor**
        if (!currentUser.getRole().equals("ADMIN") && !project.getMentor().getUserId().equals(currentUser.getUserId())) {
            throw new AccessDeniedException("You are not authorized to delete this task");
        }

        if (task.getDeletedAt() != null) {
            return false; // Already deleted
        }

        task.setDeletedAt(LocalDateTime.now());
        taskRepository.save(task);
        return true;
    }

    private TaskDTO convertToDTO(Task task) {
        TaskDTO dto = new TaskDTO();
        dto.setTaskId(task.getTaskId());
        dto.setTaskName(task.getTaskName());
        dto.setCreatedAt(task.getCreatedAt());
        dto.setDueDate(task.getDueDate());
        dto.setStudentStatus(task.getStudentStatus());
        dto.setCompleteStatus(task.getCompleteStatus());
        dto.setModifiedAt(task.getModifiedAt());
        dto.setOpenStatus(task.getOpenStatus());
        dto.setDeletedAt(task.getDeletedAt());
        dto.setTaskObjective(task.getTaskObjective());
        dto.setModifiedBy(task.getModifiedBy());

        if (task.getProject() != null) {
            dto.setProjectId(task.getProject().getProjectId());
        }

        if (task.getAttachments() != null) {
            dto.setAttachments(task.getAttachments());
        }

        return dto;
    }

    private User getCurrentUser() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByEmailAndDeletedAtIsNull(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }
}
