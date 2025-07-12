package kit.corp.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
@ConfigurationProperties()
@Data
public class TaskConfiguration {
   private SaveTask saveTask;
   private StartTask startTask;
   private boolean needAuth;

   @Data
    public static class SaveTask {
       private boolean checkAfterSave;
       private String taskName;
   }

    @Data
    public static class StartTask {
        private Duration timeDelay;
        private String taskName;
    }
}
