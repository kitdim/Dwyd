package kit.corp.model.notification;

import jakarta.persistence.*;
import lombok.*;

import static jakarta.persistence.GenerationType.IDENTITY;

@Entity
@Table(name = "price_notifications")
@Getter
@Setter
@Builder
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class PriceNotification {
    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;
    @Column(name = "all_prices")
    private String allPrices;
    @Column(name = "product_id")
    private Long productId;
    @Column(name = "user_id")
    private Long userId;
    @Column(name = "short_link")
    private String shortLink;
}
