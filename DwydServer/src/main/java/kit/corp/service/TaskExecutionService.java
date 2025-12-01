package kit.corp.service;

import kit.corp.config.TaskConfiguration;
import kit.corp.model.task.TaskExecution;
import kit.corp.model.task.TaskStatus;
import kit.corp.repository.PriceNotificationRepository;
import kit.corp.repository.TaskExecutionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Slf4j
public class TaskExecutionService {
    private final PriceNotificationRepository priceNotificationRepository;
    private final TaskExecutionRepository taskExecutionRepository;
    private final TaskConfiguration taskConfiguration;

    @Scheduled(fixedDelayString = "${tasks.clear.time-delay}", timeUnit = TimeUnit.MINUTES)
    @Async
    public void clear() {
        String taskName = taskConfiguration.getClear().getTaskName();
        String tableName = taskConfiguration.getClear().getTableName();

        if (taskExecutionRepository.isRunningTask(taskName, TaskStatus.RUNNING)) {
            log.warn("Task {} isn't completed yet.", taskName);
            return;
        }

        TaskExecution taskExecution = createTaskExecution(taskName);
        taskExecutionRepository.save(taskExecution);

        TaskResult result = process(tableName);

        completeTaskExecution(taskExecution, result);
    }

    private TaskResult process(String tableName) {
        long total;
        long updates;
        long errors;

        switch (tableName) {
            case "price_notifications":
                try {
                    total = priceNotificationRepository.count();
                    priceNotificationRepository.deleteAll();
                    errors = 0;
                    updates = total;

                } catch (Exception e) {
                    total = 0;
                    errors = 1;
                    updates = 0;

                    log.error("Delete for table {} with error: {}.\nStack trace:", tableName, e.getMessage(), e);
                }
                break;
            default:
                total = 0;
                errors = 1;
                updates = 0;

                log.debug("{} is not support", tableName);
        }

        return new TaskResult(total, updates, errors);
    }

    private TaskExecution createTaskExecution(String taskName) {
        List<String> taskParams = List.of(
                String.valueOf(taskConfiguration.getClear().getTimeDelay()),
                String.valueOf(taskConfiguration.isNeedAuth())
        );
        log.info("Task {} is running with params {}.", taskName, taskParams);

        return TaskExecution.builder()
                .taskName(taskName)
                .taskStatus(TaskStatus.RUNNING)
                .startTime(new Timestamp(System.currentTimeMillis()))
                .startParams(String.join(";", taskParams))
                .build();
    }

    private void completeTaskExecution(TaskExecution taskExecution, TaskResult result) {
        String description = String.format(
                "Count elems: %d%nCount error: %d%nCount update: %d%n",
                result.total(), result.errors(), result.updates()
        );

        TaskStatus status = result.errors() > 0 ? TaskStatus.ERROR : TaskStatus.FINISH;

        taskExecution.setFinishTime(new Timestamp(System.currentTimeMillis()));
        taskExecution.setDescription(description);
        taskExecution.setTaskStatus(status);

        log.info(description);
        taskExecutionRepository.save(taskExecution);
    }

    private record TaskResult(long total, long updates, long errors) {
    }
}
