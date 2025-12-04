package kit.corp.bd;

import kit.corp.repository.PriceNotificationRepository;
import kit.corp.service.PriceNotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class PriceNotificationRepositoryTest {
    @Autowired
    private PriceNotificationRepository priceNotificationRepository;
    @Autowired
    private PriceNotificationService priceNotificationService;


}
