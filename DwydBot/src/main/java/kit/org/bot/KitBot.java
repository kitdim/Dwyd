package kit.org.bot;

import com.fasterxml.jackson.databind.ObjectMapper;
import kit.org.bot.model.dto.ProductSave;
import kit.org.config.BotConfiguration;
import lombok.Getter;
import lombok.Setter;
import org.telegram.telegrambots.client.okhttp.OkHttpTelegramClient;
import org.telegram.telegrambots.longpolling.util.LongPollingSingleThreadUpdateConsumer;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import java.io.File;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Getter
@Setter
public class KitBot implements LongPollingSingleThreadUpdateConsumer {
    private final TelegramClient telegramClient;
    private final Map<Long, Boolean> waitingForProduct;
    private final KitBotConfig myConfig;
    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;

    public KitBot(BotConfiguration configuration) {
        waitingForProduct = new ConcurrentHashMap<>();
        myConfig = initBotConfig(configuration);
        telegramClient = new OkHttpTelegramClient(myConfig.botToken);
        httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(10))
                .build();
        objectMapper = new ObjectMapper();
    }

    @Override
    public void consume(List<Update> updates) {
        LongPollingSingleThreadUpdateConsumer.super.consume(updates);
    }

    @Override
    public void consume(Update update) {
        // Create the keyboard (list of keyboard rows)
        List<KeyboardRow> keyboard = new ArrayList<>();
        // Create a keyboard row
        KeyboardRow row = new KeyboardRow();
        // Set each button
        row.add(myConfig.textMessagesAndButtons.get("AddProductNameButton").getFirst());
        row.add(myConfig.textMessagesAndButtons.get("HelpNameButton").getFirst());
        keyboard.add(row);

        ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup(keyboard);
        keyboardMarkup.setResizeKeyboard(true);

        if (update.hasMessage() && update.getMessage().hasText()) {
            String messageText = update.getMessage().getText();
            long chatId = update.getMessage().getChatId();

            // Если пользователь ожидает ввода товара
            if (waitingForProduct.getOrDefault(chatId, false)) {
                // Здесь можно обработать товар (например, сохранить или отправить админу)
                waitingForProduct.put(chatId, false); // Сбросить ожидание
                boolean isSuccess = false;

                if (!messageText.isEmpty()) {
                    try {
                        String[] bunch = messageText.split(" ");
                        String marketCheckTypeAfterPreprocess = getMarket(bunch[0]);
                        ProductSave productSave = new ProductSave(marketCheckTypeAfterPreprocess, bunch[0], bunch[1]);
                        sendRequest(productSave, myConfig.url);
                        isSuccess = true;
                    } catch (RuntimeException e) {
                        e.printStackTrace();
                    }
                }

                String textSend = isSuccess ?
                        myConfig.textMessagesAndButtons.get("AnswerYesMessage").getFirst() :
                        myConfig.textMessagesAndButtons.get("AnswerNoMessage").getFirst();

                SendMessage message = SendMessage.builder()
                        .chatId(chatId)
                        .text(textSend)
                        .replyMarkup(keyboardMarkup)
                        .build();
                try {
                    telegramClient.execute(message);
                } catch (TelegramApiException e) {
                    e.printStackTrace();
                }
                return;
            }

            String answer = switch (messageText) {
                case "/start" -> myConfig.textMessagesAndButtons.get("WelcomeMessage").getFirst();
                case "Добавить товар" -> {
                    waitingForProduct.put(chatId, true); // Включить ожидание товара
                    yield myConfig.textMessagesAndButtons.get("InstructionMessage").getFirst();
                }
                case "Помощь" ->  {
                    SendPhoto photo = SendPhoto.builder()
                            .chatId(chatId)
                            .photo(new InputFile(new File(getClass().getClassLoader().getResource("helpPhoto.jpg").getFile())))
                            .caption(myConfig.textMessagesAndButtons.get("HelpMessage").getFirst())
                            .replyMarkup(keyboardMarkup)
                            .build();
                    try {
                        telegramClient.execute(photo);
                    } catch (TelegramApiException e) {
                        e.printStackTrace();
                    }
                    yield "";
                }
                default -> "";
            };

            if (answer.isEmpty()) {
                return;
            }

            SendMessage message = SendMessage // Create a message object
                    .builder()
                    .chatId(chatId)
                    .text(answer)
                    .replyMarkup(keyboardMarkup)
                    .build();
            try {
                telegramClient.execute(message); // Sending our message object to user
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        }
    }

    private String getMarket(String bunch) {
        if (bunch.contains("ozon")) {
            return "OZON";
        } else if (bunch.contains("yandex")) {
            return "YANDEX";
        } else if (bunch.contains("wildberries")) {
            return "WB";
        } else {
            throw new IllegalStateException();
        }
    }

    private HttpResponse<String> sendRequest(ProductSave productSave, String url) {
        try {
            // Конвертируем объект в JSON
            String requestBody = objectMapper.writeValueAsString(productSave);

            // Создаем HTTP запрос
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("Content-Type", "application/json")
                    .header("Accept", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                    .build();

            // Отправляем запрос и получаем ответ
            return httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        } catch (Exception e) {
            throw new RuntimeException("Ошибка при отправке запроса: " + e.getMessage(), e);
        }
    }

    private KitBotConfig initBotConfig(BotConfiguration configuration) {
        Map<String, List<String>> textMessagesAndButtons = new HashMap<>();
        textMessagesAndButtons.put("WelcomeMessage", List.of(configuration.getWelcomeMessage()));
        textMessagesAndButtons.put("HelpMessage", List.of(configuration.getHelpMessage()));
        textMessagesAndButtons.put("InstructionMessage", List.of(configuration.getInstructionMessage()));
        textMessagesAndButtons.put("AnswerYesMessage", List.of(configuration.getAnswerYesMessage()));
        textMessagesAndButtons.put("AnswerNoMessage", List.of(configuration.getAnswerNoMessage()));
        textMessagesAndButtons.put("AddProductNameButton", List.of(configuration.getAddProductNameButton()));
        textMessagesAndButtons.put("HelpNameButton", List.of(configuration.getHelpNameButton()));

        return new KitBotConfig(configuration.getBotName(), configuration.getBotUsername(), configuration.getBotToken(), configuration.getUrlServer(), textMessagesAndButtons, configuration.getAdminId());
    }

    private static class KitBotConfig {
        private String botName;
        private String botUserName;
        private String botToken;
        private String url;
        private Map<String, List<String>> textMessagesAndButtons;
        private Long adminId;

        public KitBotConfig(String botName, String botUserName, String botToken, String url, Map<String, List<String>> textMessagesAndButtons, Long adminId) {
            this.botName = botName;
            this.botUserName = botUserName;
            this.botToken = botToken;
            this.url = url;
            this.textMessagesAndButtons = textMessagesAndButtons;
            this.adminId = adminId;
        }
    }
}
