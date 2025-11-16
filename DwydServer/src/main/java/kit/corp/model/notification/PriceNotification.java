package kit.corp.model.notification;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import static jakarta.persistence.GenerationType.IDENTITY;

@Entity
@Table(name = "price_notifications")
@Getter
@Setter
@Builder
@ToString
public class PriceNotification {
    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;
    @Column(name = "all_price")
    private String allPrices;
    @Column(name = "product_id")
    private Long productId;
    @Column(name = "user_id")
    private Long userId;
    @Column(name = "is_success_send")
    private Boolean isSuccessSend;
}
