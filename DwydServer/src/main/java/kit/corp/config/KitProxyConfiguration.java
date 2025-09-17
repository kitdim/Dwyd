package kit.corp.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@ConfigurationProperties(prefix = "kitproxy")
@Data
@Deprecated
public class KitProxyConfiguration {
    private List<String> urlsOzon;
    private List<String> urlsWb;
    private String initScript;
    private BrowserOption browserOption;
    private ContextOption contextOption;
    private HumanLikeInteraction humanLikeInteraction;

    @Data
    public static class BrowserOption {
        private Boolean headless;
        private List<String> args;
    }

    @Data
    public static class ContextOption {
        private String userAgent;
        private List<Integer> viewPortSize;
        private Boolean bypassCsp;
        private Boolean ignoreHttpsErrors;
    }

    @Data
    public static class HumanLikeInteraction {
        private List<Integer> firstWaitTimeout;
        private List<Integer> mouseMove;
        private List<Integer> secondWaitTimeout;
        private List<Integer> mouseWheel;
        private Integer thirdWaitTimeout;
    }
}
