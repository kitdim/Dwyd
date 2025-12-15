package kit.org.service;

import kit.org.bot.model.User;
import kit.org.config.BotConfiguration;
import kit.org.repository.UserRepository;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NotificationWorkerService {
    private static final String URL = new BotConfiguration().getUrlsServer().getLast();
    private final UserRepository userRepository = UserRepository.getInstance();

    public void process() {
        Map<Long, HttpResponse<String>> clientMap = sendRequestsToServer();
        // TODO дописать отправку уведомелния для пользователя
    }

    private Map<Long, HttpResponse<String>> sendRequestsToServer() {
        Map<Long, HttpResponse<String>> clientMap = new HashMap<>();
        List<Long> ids = userRepository.findAll().stream().map(User::getChatId).toList();
        HttpClient httpClient = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(10)).build();

        for (Long id : ids) {
            String fullUrl = URL.concat("\\" + id);

            try {
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(fullUrl))
                        .header("Content-Type", "application/json")
                        .header("Accept", "application/json")
                        .GET()
                        .build();

                HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
                clientMap.put(id, response);

            } catch (Exception e) {
                throw new RuntimeException("Ошибка при отправке запроса: " + e.getMessage(), e);
            }
        }

        return clientMap;
    }
}
