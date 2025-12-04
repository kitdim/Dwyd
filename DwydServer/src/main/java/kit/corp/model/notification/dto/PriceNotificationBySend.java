package kit.corp.model.notification.dto;

public record PriceNotificationBySend (String allPrices,
                                       Long productId,
                                       Long userId,
                                       String shortLink) {
}
