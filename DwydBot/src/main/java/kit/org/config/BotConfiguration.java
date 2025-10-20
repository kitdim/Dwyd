package kit.org.config;

import io.github.cdimascio.dotenv.Dotenv;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

@Slf4j
@Getter
public class BotConfiguration {
    private String botName;
    private String botToken;
    private String botUsername;
    private Long adminId;
    private String welcomeMessage;
    private String helpMessage;
    private String urlServer;

    private static final String CONFIG_FILE = "/bot.properties";
    private static final Dotenv dotenv = Dotenv.load();

    public BotConfiguration() {
        loadConfig();
    }

    private void loadConfig() {
        Properties properties = new Properties();

        try (InputStream input = getClass().getResourceAsStream(CONFIG_FILE)) {
            if (input == null) {
                throw new RuntimeException("Config file not found: " + CONFIG_FILE);
            }

            try (Reader reader = new InputStreamReader(input, StandardCharsets.UTF_8)) {
                properties.load(reader);
            }

            // Обязательные параметры
            botToken = dotenv.get("BOT_TOKEN");
            botUsername = dotenv.get("BOT_USERNAME");
            botName = dotenv.get("BOT_NAME");
            urlServer = getRequiredProperty(properties, "server.url.save");

            validate();

            // Опциональные параметры
            adminId = getLongProperty(properties, "bot.admin.id");
            welcomeMessage = properties.getProperty("bot.message.welcome", "Hi");
            helpMessage = properties.getProperty("bot.message.help", "It works like this.");

            log.info("Конфигурация загружена: бот @{}", botUsername);

        } catch (IOException e) {
            throw new RuntimeException("Ошибка загрузки конфигурации: " + e.getMessage(), e);
        }
    }


    private String getRequiredProperty(Properties properties, String key) {
        String value = properties.getProperty(key);
        if (value == null || value.trim().isEmpty()) {
            throw new RuntimeException("Обязательный параметр отсутствует: " + key);
        }
        return value.trim();
    }

    private Long getLongProperty(Properties properties, String key) {
        String value = properties.getProperty(key);
        if (value != null && !value.trim().isEmpty()) {
            try {
                return Long.parseLong(value.trim());
            } catch (NumberFormatException e) {
                log.warn("Некорректное числовое значение для {}: {}", key, value);
            }
        }
        return null;
    }

    private void validate() {
        if (botToken == null || botToken.isEmpty()) {
            throw new IllegalStateException("Токен бота не настроен");
        }
        if (botUsername == null || botUsername.isEmpty()) {
            throw new IllegalStateException("Имя бота не настроено");
        }
        if (urlServer == null || urlServer.isEmpty()) {
            throw new IllegalStateException("Адрес сервера для отправки не настроен");
        }
    }
}
