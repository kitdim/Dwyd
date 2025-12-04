package kit.corp.service;

import kit.corp.model.notification.PriceNotification;
import kit.corp.model.notification.dto.PriceNotificationBySend;
import kit.corp.model.product.Product;
import kit.corp.repository.PriceNotificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class PriceNotificationService {
    private final PriceNotificationRepository priceNotificationRepository;

    public List<PriceNotificationBySend> getPriceNotificationsByUserId(Long userId) {
        return priceNotificationRepository.findAllByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Not Found: " + userId)).stream()
                .map(value -> new PriceNotificationBySend(
                        value.getAllPrices(),
                        value.getProductId(),
                        value.getUserId(),
                        value.getShortLink())
                ).toList();
    }

    public void saveNotification(Product productNotifications) {
        String allPricesText = String.format(
                "price:%.2f|priceWithDiscount:%.2f|lastPrice:%.2f",
                productNotifications.getPrice(),
                productNotifications.getPriceWithDiscount(),
                productNotifications.getLastPrice());

        PriceNotification notification = PriceNotification.builder()
                .allPrices(allPricesText)
                .userId(productNotifications.getUserId())
                .productId(productNotifications.getId())
                .shortLink(productNotifications.getShortLink())
                .build();

        try {
            priceNotificationRepository.save(notification);
            log.debug("Save notification: {}.", notification);
        } catch (Exception e) {
            log.error("{} with error: {}.\nStack trace:", notification, e.getMessage(), e);
        }
    }
}
