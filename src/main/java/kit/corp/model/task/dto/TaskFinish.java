package kit.corp.model.task.dto;

import kit.corp.model.task.TaskStatus;

import java.sql.Timestamp;

public record TaskFinish(String description,
                         TaskStatus taskStatus,
                         Timestamp finishTime) {
}
