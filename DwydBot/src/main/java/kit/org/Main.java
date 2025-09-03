package kit.org;

import kit.org.bot.KitBot;
import kit.org.config.BotConfiguration;
import org.telegram.telegrambots.longpolling.TelegramBotsLongPollingApplication;

public class Main {
    public static void main(String[] args) {

        BotConfiguration configuration = new BotConfiguration();

        try (TelegramBotsLongPollingApplication botsApplication = new TelegramBotsLongPollingApplication()) {
            botsApplication.registerBot(configuration.getBotToken(), new KitBot(configuration));
            System.out.println("MyAmazingBot successfully started!");

            Thread.currentThread().join();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}