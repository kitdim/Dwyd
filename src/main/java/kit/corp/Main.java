package kit.corp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.PropertySource;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@PropertySource("classpath:task_startup/conf.yml")
public class Main {
    public static void main(String[] args) {
        SpringApplication.run(Main.class, args);
    }
}
