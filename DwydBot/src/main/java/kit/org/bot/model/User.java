package kit.org.bot.model;

import java.util.Objects;

public class User {
    private Long chatId;
    private String marketType;
    private String article;
    private String shortLink;

    public User() {

    }

    public User(Long chatId, String marketType, String article, String shortLink) {
        this.chatId = chatId;
        this.marketType = marketType;
        this.article = article;
        this.shortLink = shortLink;
    }

    public Long getChatId() {
        return chatId;
    }

    public String getMarketType() {
        return marketType;
    }

    public String getArticle() {
        return article;
    }

    public String getShortLink() {
        return shortLink;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        User user = (User) o;
        return Objects.equals(chatId, user.chatId) && Objects.equals(marketType, user.marketType) && Objects.equals(article, user.article) && Objects.equals(shortLink, user.shortLink);
    }

    @Override
    public int hashCode() {
        return Objects.hash(chatId, marketType, article, shortLink);
    }
}
