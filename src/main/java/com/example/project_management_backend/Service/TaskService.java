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

    
    @Transactional
    public TaskDTO createTask(TaskDTO taskDTO) {
      
        User user = getAuthenticatedUser();

    
        Project project = projectRepository.findById(taskDTO.getProjectId())
                .orElseThrow(() -> new RuntimeException("Project not found"));

        
        if (!user.getRole().getRoleName().equals("ADMIN") && !user.getUserId().equals(project.getMentor().getUserId())) {
            throw new RuntimeException("Only ADMIN or assigned mentor can create tasks");
        }

         boolean taskExists = taskRepository.existsByTaskNameAndProject_ProjectId(taskDTO.getTaskName(), taskDTO.getProjectId());

        if (taskExists) {
        throw new RuntimeException("Task with the same name already exists in this project. Choose a different name.");
    }
        Task task = new Task();
        task.setTaskName(taskDTO.getTaskName());
        task.setTaskObjective(taskDTO.getTaskObjective());
        task.setDueDate(taskDTO.getDueDate());
        task.setCompleteStatus("Not Completed"); 
        task.setProject(project);
        task.setCreatedAt(LocalDateTime.now());

       
        Task savedTask = taskRepository.save(task);
        return convertToDTO(savedTask);
    }
    

    

    @Transactional
    public TaskDTO updateTask(UUID taskId, TaskDTO taskDTO) {
        
        User user = getAuthenticatedUser();

    
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Task not found"));

    
        Project project = task.getProject();

     
        boolean isAdmin = user.getRole().getRoleName().equals("ADMIN");
        boolean isAssignedMentor = user.getUserId().equals(project.getMentor().getUserId());
        boolean isAssignedStudent = isAssignedStudent(project.getProjectId());

        if (!isAdmin && !isAssignedMentor && !isAssignedStudent) {
            throw new RuntimeException("You are not authorized to update this task");
        }

        
        if (isAdmin || isAssignedMentor) {
            
            task.setTaskName(taskDTO.getTaskName());
            task.setTaskObjective(taskDTO.getTaskObjective());
            task.setDueDate(taskDTO.getDueDate());
            task.setCompleteStatus(taskDTO.getCompleteStatus());
        } else if (isAssignedStudent) {
           
        if (task.getDueDate().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("The due date has passed. You can no longer update attachments or student status.");
        }
            
            task.setAttachments(taskDTO.getAttachments()); 
            task.setStudentStatus(taskDTO.getStudentStatus());
        }

        task.setModifiedAt(LocalDateTime.now());
        task.setModifiedBy(user.getUserId());

       
        Task updatedTask = taskRepository.save(task);
        return convertToDTO(updatedTask);
    }

   
    @Transactional
    public List<TaskDTO> getTasksByProjectId(UUID projectId) {
   
        User user = getAuthenticatedUser();

   
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Project not found"));

        
        boolean isAdmin = user.getRole().getRoleName().equals("ADMIN");
        boolean isAssignedMentor = user.getUserId().equals(project.getMentor().getUserId());
        boolean isAssignedStudent = isAssignedStudent(project.getProjectId());

        if (!isAdmin && !isAssignedMentor && !isAssignedStudent) {
            throw new RuntimeException("You are not authorized to view tasks for this project");
        }

     
        return taskRepository.findByProject_ProjectId(projectId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    
    @Transactional
    public void deleteTask(UUID taskId) {
      
        User user = getAuthenticatedUser();

  
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Task not found"));

        Project project = task.getProject();

    
        if (!user.getRole().getRoleName().equals("ADMIN") && !user.getUserId().equals(project.getMentor().getUserId())) {
            throw new RuntimeException("Only ADMIN or assigned mentor can delete tasks");
        }

  
        task.setDeletedAt(LocalDateTime.now());
        taskRepository.save(task);
    }


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

  
    private User getAuthenticatedUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByEmailAndDeletedAtIsNull(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

   
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