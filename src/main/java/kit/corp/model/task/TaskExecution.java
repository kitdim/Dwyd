package kit.corp.model.task;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;

import static jakarta.persistence.GenerationType.IDENTITY;

@Entity
@Table(name = "task_executions")
@Getter
@Setter
public class TaskExecution {
    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;
    @Column(name = "task_name")
    private String taskName;
    @Column(name = "start_time")
    private Timestamp startTime;
    @Column(name = "finish_time")
    private Timestamp finishTime;
    @Column(name = "task_status")
    @Enumerated(EnumType.STRING)
    private TaskStatus taskStatus;
    @Column(name = "start_params")
    private String startParams;
    @Column(name = "description")
    private String description;
}
