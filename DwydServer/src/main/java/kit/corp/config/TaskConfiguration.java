package kit.corp.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "tasks")
@Data
public class TaskConfiguration {
    private Save save;
    private Start start;
    private Clear clear;
    private boolean needAuth;

    @Data
    @ConfigurationProperties(prefix = "tasks.save")
    public static class Save {
        private boolean checkAfterSave;
        private String taskName;
    }

    @Data
    @ConfigurationProperties(prefix = "tasks.start")
    public static class Start {
        private long timeDelay;
        private String taskName;
    }

    @Data
    @ConfigurationProperties(prefix = "tasks.clear")
    public static class Clear {
        private String taskName;
        private String tableName;
        private long timeDelay;
    }
}
