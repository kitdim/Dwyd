package kit.corp.service;

import kit.corp.config.TaskConfiguration;
import kit.corp.repository.PriceNotificationRepository;
import kit.corp.repository.TaskExecutionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

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
        priceNotificationRepository.deleteAll();
    }
}
