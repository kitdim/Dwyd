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
    private double price;
    @Column(name = "price_with_discount")
    private double priceWithDiscount;
    @Column(name = "last_price")
    private double lastPrice;
    private String article;
    @Enumerated(EnumType.STRING)
    private MarketCheckType market;
    @Column(name = "check_time")
    private Timestamp checkTime;
}
