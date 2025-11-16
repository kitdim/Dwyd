package kit.corp.model.product;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import kit.corp.freebie.MarketCheckType;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;

import static jakarta.persistence.GenerationType.IDENTITY;

@Entity
@Table(name = "products")
@Getter
@Setter
public class Product {
    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;
    private Double price;
    @Column(name = "price_with_discount")
    private Double priceWithDiscount;
    @Column(name = "last_price")
    private Double lastPrice;
    private String article;
    @Column(name = "short_link")
    private String shortLink;
    @Enumerated(EnumType.STRING)
    private MarketCheckType market;
    @Column(name = "check_time")
    private Timestamp checkTime;
    @Enumerated(EnumType.STRING)
    @Column(name = "product_process_type")
    private ProductProcessType productProcessType;
    @Column(name = "user_id")
    private Long userId;

    @Override
    public String toString() {
        return "Product{"
                + "id=" + id
                + ", price=" + price
                + ", priceWithDiscount=" + priceWithDiscount
                + ", lastPrice=" + lastPrice
                + ", shortLink=" + shortLink
                + ", article='" + article + '\''
                + ", market=" + market
                + ", checkTime=" + checkTime
                + ", productProcessType=" + productProcessType
                + ", userId=" + userId
                + '}';
    }
}
