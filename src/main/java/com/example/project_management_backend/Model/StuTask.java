package com.example.project_management_backend.Model;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "stu_task")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StuTask {

    @EmbeddedId
    private StuTaskId id;

    @ManyToOne
    @MapsId("studentId")
    @JoinColumn(name = "student_id", nullable = false)
    private User student;

    @ManyToOne
    @MapsId("taskId")
    @JoinColumn(name = "task_id", nullable = false)
    private Task task;

    public StuTask(User student, Task task) {
        this.id = new StuTaskId(student.getUserId(), task.getTaskId()); // Initializes composite key
        this.student = student;
        this.task = task;
    }
}
