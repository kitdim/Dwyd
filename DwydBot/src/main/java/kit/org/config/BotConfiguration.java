package kit.org.config;

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

    private static final String CONFIG_FILE = "/bot.properties";

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
            botToken = getRequiredProperty(properties, "bot.token");
            botUsername = getRequiredProperty(properties, "bot.username");
            botName = getRequiredProperty(properties, "bot.name");

            validate();

            // Опциональные параметры
            adminId = getLongProperty(properties, "bot.admin.id");
            welcomeMessage = properties.getProperty("bot.welcome.message", "Добро пожаловать!");

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

    private int getIntProperty(Properties properties, String key, int defaultValue) {
        String value = properties.getProperty(key);
        if (value != null && !value.trim().isEmpty()) {
            try {
                return Integer.parseInt(value.trim());
            } catch (NumberFormatException e) {
                log.warn("Некорректное числовое значение для {}: {}", key, value);
            }
        }
        return defaultValue;
    }

    private void validate() {
        if (botToken == null || botToken.isEmpty()) {
            throw new IllegalStateException("Токен бота не настроен");
        }
        if (botUsername == null || botUsername.isEmpty()) {
            throw new IllegalStateException("Имя бота не настроено");
        }
    }
}
