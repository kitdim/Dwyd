package kit.corp.model.task.dto;

import kit.corp.model.task.TaskStatus;

import java.sql.Timestamp;
import java.util.List;

public record TaskStart(String taskName,
                        TaskStatus taskStatus,
                        List<String> taskParams,
                        Timestamp startTime) {
}
