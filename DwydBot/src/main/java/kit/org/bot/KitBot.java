package kit.org.bot;

import kit.org.config.BotConfiguration;
import lombok.Getter;
import lombok.Setter;
import org.telegram.telegrambots.client.okhttp.OkHttpTelegramClient;
import org.telegram.telegrambots.longpolling.util.LongPollingSingleThreadUpdateConsumer;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
@Setter
public class KitBot implements LongPollingSingleThreadUpdateConsumer {
    private final TelegramClient telegramClient;
    private final KitBotConfig myConfig;

    public KitBot(BotConfiguration configuration) {
        this.myConfig = initBotConfig(configuration);
        this.telegramClient = new OkHttpTelegramClient(myConfig.botToken);
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
        row.add("Добавить товар");
        row.add("Помощь");
        keyboard.add(row);

        ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup(keyboard);
        keyboardMarkup.setResizeKeyboard(true);

        if (update.hasMessage() && update.getMessage().hasText()) {
            String messageText = update.getMessage().getText();
            long chatId = update.getMessage().getChatId();

            String answer = switch (messageText) {
                case "/start" -> myConfig.messageText.get("WelcomeMessage").getFirst();
                case "Добавить товар" -> "отправь ссылку на товар и артикль товара";
                case "Помощь" -> "Делается так";
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

    private KitBotConfig initBotConfig(BotConfiguration configuration) {
        Map<String, List<String>> entities = new HashMap<>();
        entities.put("WelcomeMessage", List.of(configuration.getWelcomeMessage()));

        return new KitBotConfig(configuration.getBotName(), configuration.getBotUsername(), configuration.getBotToken(), entities, configuration.getAdminId());
    }

    private static class KitBotConfig {
        private String botName;
        private String botUserName;
        private String botToken;
        private Map<String, List<String>> messageText;
        private Long adminId;

        public KitBotConfig(String botName, String botUserName, String botToken, Map<String, List<String>> messageText, Long adminId) {
            this.botName = botName;
            this.botUserName = botUserName;
            this.botToken = botToken;
            this.messageText = messageText;
            this.adminId = adminId;
        }
    }
}
