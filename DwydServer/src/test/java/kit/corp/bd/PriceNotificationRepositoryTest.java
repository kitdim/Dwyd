package kit.corp.bd;

import kit.corp.model.notification.dto.PriceNotificationBySend;
import kit.corp.model.product.Product;
import kit.corp.repository.PriceNotificationRepository;
import kit.corp.service.PriceNotificationService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.stream.Stream;

@SpringBootTest
public class PriceNotificationRepositoryTest {
    @Autowired
    private PriceNotificationRepository priceNotificationRepository;
    @Autowired
    private PriceNotificationService priceNotificationService;

    @AfterEach
    public void cleanUp() {
        priceNotificationRepository.deleteAll();
    }

    @ParameterizedTest
    @MethodSource("getGoodData")
    public void saveNotificationTestWithGoodData(Double price1,
                                                 Double price2,
                                                 Double price3,
                                                 Long userId,
                                                 Long productId,
                                                 String shortLink,
                                                 PriceNotificationBySend priceNotificationBySend) {
        Product product = new Product();
        product.setPrice(price1);
        product.setPriceWithDiscount(price2);
        product.setLastPrice(price3);
        product.setId(userId);
        

        priceNotificationService.saveNotification();
    }

    public static Stream<Arguments> getGoodData() {
        return Stream.of(
                Arguments.of(2000, 2000, 2000, 12L, 11L, "https://market.yandex.ru/cc/7aVTbX",
                        new PriceNotificationBySend("{}", 12L, 12L, "https://market.yandex.ru/cc/7aVTbX")),
                Arguments.of(0, 0, 0, 12L, 10L, "https://market.yandex.ru/cc/7AVTbX",
                        new PriceNotificationBySend("{}", 12L, 12L, "https://market.yandex.ru/cc/7AVTbX"))
        );
    }
}
