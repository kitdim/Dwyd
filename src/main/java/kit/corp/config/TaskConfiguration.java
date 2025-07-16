package kit.corp.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
@ConfigurationProperties(prefix = "tasks")
@Data
public class TaskConfiguration {
   private Save save;
   private Start start;
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
}
