package kit.corp.model;

import kit.corp.freebie.MarketCheckType;

import java.sql.Timestamp;
import java.util.UUID;

public class Product {
    private UUID id;
    private double price;
    private double priceWithDiscount;
    private double lastPrice;
    private String article;
    private MarketCheckType market;
    private Timestamp checkTime;

    public double getPrice() {
        return price;
    }

    public double getPriceWithDiscount() {
        return priceWithDiscount;
    }

    public String getArticle() {
        return article;
    }

    public MarketCheckType getMarket() {
        return market;
    }

    public double getLastPrice() {
        return lastPrice;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public void setMarket(MarketCheckType market) {
        this.market = market;
    }

    public void setPriceWithDiscount(double priceWithDiscount) {
        this.priceWithDiscount = priceWithDiscount;
    }

    public void setLastPrice(double lastPrice) {
        this.lastPrice = lastPrice;
    }

    public void setArticle(String article) {
        this.article = article;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public Timestamp getCheckTime() {
        return checkTime;
    }

    public void setCheckTime(Timestamp checkTime) {
        this.checkTime = checkTime;
    }

    @Override
    public String toString() {
        return "Product{" +
                "id=" + id +
                ", price=" + price +
                ", priceWithDiscount=" + priceWithDiscount +
                ", lastPrice=" + lastPrice +
                ", article='" + article + '\'' +
                ", market=" + market.toString() +
                ", checkTime=" + checkTime +
                '}';
    }
}
