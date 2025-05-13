package kit.corp.model;

import jakarta.persistence.*;
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
