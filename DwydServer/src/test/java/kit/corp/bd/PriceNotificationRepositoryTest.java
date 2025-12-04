package kit.corp.bd;

import kit.corp.model.notification.PriceNotification;
import kit.corp.model.notification.dto.PriceNotificationBySend;
import kit.corp.model.product.Product;
import kit.corp.repository.PriceNotificationRepository;
import kit.corp.service.PriceNotificationService;
import kit.corp.service.TaskExecutionService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
public class PriceNotificationRepositoryTest {
    @Autowired
    private PriceNotificationRepository priceNotificationRepository;
    @Autowired
    private PriceNotificationService priceNotificationService;
    @Autowired
    private TaskExecutionService taskExecutionService;

    @AfterEach
    public void cleanUp() {
        priceNotificationRepository.deleteAll();
    }

    @ParameterizedTest(name = "Test by saveNotification method with good data, with param: {0}, {1}, {2}, {3}, {4}, {5}, {6}, {7}")
    @MethodSource("getGoodData")
    public void saveNotificationTestWithGoodData(Double price1,
                                                 Double price2,
                                                 Double price3,
                                                 Long productId,
                                                 Long userId,
                                                 String shortLink,
                                                 PriceNotificationBySend priceNotificationBySend) {
        Product product = new Product();
        product.setPrice(price1);
        product.setPriceWithDiscount(price2);
        product.setLastPrice(price3);
        product.setUserId(userId);
        product.setId(productId);
        product.setShortLink(shortLink);

        priceNotificationService.saveNotification(product);
        PriceNotification notificationFromBd = priceNotificationRepository.findAll().getFirst();

        assertEquals(notificationFromBd.getAllPrices(), priceNotificationBySend.allPrices());
        assertEquals(notificationFromBd.getUserId(), priceNotificationBySend.userId());
        assertEquals(notificationFromBd.getProductId(), priceNotificationBySend.productId());
        assertEquals(notificationFromBd.getShortLink(), priceNotificationBySend.shortLink());
    }

    @Test
    @DisplayName("Test for add and clear by table price_notifications")
    public void clearTablePriceNotificationTest() {
        List<PriceNotification> priceNotificationListByTest = List.of(
                new PriceNotification(null, "price:2000,00|priceWithDiscount:2000,00|lastPrice:2000,00", 12L, 11L, "https://market.yandex.ru/cc/7aVTbX"),
                new PriceNotification(null, "price:2000,00|priceWithDiscount:2000,00|lastPrice:2000,00", 13L, 11L, "https://market.yandex.ru/cc/7aVTbX"),
                new PriceNotification(null, "price:2000,00|priceWithDiscount:2000,00|lastPrice:2000,00", 14L, 11L, "https://market.yandex.ru/cc/7aVTbX"));

        priceNotificationListByTest.forEach(priceNotification ->  priceNotificationRepository.saveAndFlush(priceNotification));
        assertEquals(priceNotificationListByTest.size(), priceNotificationRepository.findAll().size());

        taskExecutionService.clear();

        sleepBy5Sec();
        assertTrue(priceNotificationRepository.findAll().isEmpty());
    }

    private void sleepBy5Sec() {
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private void sleepByTime() {
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public static Stream<Arguments> getGoodData() {
        return Stream.of(
                Arguments.of(2000.00, 2000.00, 2000.00, 12L, 11L, "https://market.yandex.ru/cc/7aVTbX",
                        new PriceNotificationBySend("price:2000,00|priceWithDiscount:2000,00|lastPrice:2000,00", 12L, 11L, "https://market.yandex.ru/cc/7aVTbX")),
                Arguments.of(0.00, 0.00, 0.00, 12L, 10L, "https://market.yandex.ru/cc/7AVTbX",
                        new PriceNotificationBySend("price:0,00|priceWithDiscount:0,00|lastPrice:0,00", 12L, 10L, "https://market.yandex.ru/cc/7AVTbX"))
        );
    }
}
