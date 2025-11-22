package kit.corp.bd;

import kit.corp.freebie.MarketCheckType;
import kit.corp.model.product.Product;
import kit.corp.model.product.ProductProcessType;
import kit.corp.model.product.dto.SaveNewProduct;
import kit.corp.repository.ProductRepository;
import kit.corp.repository.TaskExecutionRepository;
import kit.corp.service.CheckService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
public class ProductRepositoryTest {
    private final List<SaveNewProduct> saveNewProducts = new ArrayList<>();
    private final List<SaveNewProduct> saveNewProductsWithBadData = new ArrayList<>();

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private TaskExecutionRepository taskExecutionRepository;

    @Autowired
    private CheckService checkService;

    @AfterEach
    public void cleanUp() {
        taskExecutionRepository.deleteAll();
        productRepository.deleteAll();

        List<Product> remainingProducts = productRepository.findAll();
        assertEquals(0, remainingProducts.size(), "База данных должна быть полностью очищена после теста");
    }

    @BeforeEach
    public void setUp() {
        cleanUp();

        saveNewProducts.clear();
        saveNewProductsWithBadData.clear();

        saveNewProducts.add(new SaveNewProduct(MarketCheckType.YANDEX, "103797360000", "https://market.yandex.ru/cc/7aVTbX", 12L));
        saveNewProducts.add(new SaveNewProduct(MarketCheckType.YANDEX, "4514870086", "https://market.yandex.ru/cc/7cDixu", 13L));
        saveNewProducts.add(new SaveNewProduct(MarketCheckType.YANDEX, "103577166537", "https://market.yandex.ru/cc/7bbmmd", 14L));

        saveNewProductsWithBadData.add(new SaveNewProduct(MarketCheckType.YANDEX, "10379736325", "https://market.yandex.ru/cc/7aVTbX", 15L));
        saveNewProductsWithBadData.add(new SaveNewProduct(MarketCheckType.YANDEX, "45141441", "https://market.yandex.ru/cc/7cDixu", 16L));
        saveNewProductsWithBadData.add(new SaveNewProduct(MarketCheckType.YANDEX, "10357712312", "https://market.yandex.ru/cc/7bbmmd", 112L));
    }

    @Test
    @DisplayName("Save test")
    public void saveTest() {
        saveNewProducts.forEach(product -> checkService.saveNew(product));

        List<Product> productListAfterSave = productRepository.findAll();

        assertEquals(saveNewProducts.size(), productListAfterSave.size());
        assertEquals(saveNewProducts.getFirst().article(), productListAfterSave.getFirst().getArticle());
    }

    @Test
    @DisplayName("Start test")
    public void startTest() {
        saveNewProducts.forEach(product -> checkService.saveNew(product));
        checkService.start();
        sleep5Sec();

        List<Product> productListAfterCheck = productRepository.findAll();
        productListAfterCheck.forEach(product -> {
            assertNotNull(product.getCheckTime());
            assertEquals(ProductProcessType.SALE, product.getProductProcessType());
        });
    }

    @Test
    @DisplayName("Start test with bad data")
    public void startWithBadData() {
        saveNewProductsWithBadData.forEach(product -> checkService.saveNew(product));
        checkService.start();
        sleep5Sec();

        List<Product> productListAfterCheck = productRepository.findAll();
        productListAfterCheck.forEach(product -> {
            assertEquals(ProductProcessType.NOT_FOUND, product.getProductProcessType());
        });
    }

    private void sleep5Sec() {
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}