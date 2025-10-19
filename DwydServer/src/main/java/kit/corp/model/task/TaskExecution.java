package kit.corp.model.task;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Timestamp;

import static jakarta.persistence.GenerationType.IDENTITY;

@Entity
@Table(name = "task_executions")
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
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
