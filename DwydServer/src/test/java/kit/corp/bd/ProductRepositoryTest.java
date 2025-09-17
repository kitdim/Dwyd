package kit.corp.bd;

import kit.corp.freebie.MarketCheckType;
import kit.corp.model.product.Product;
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
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private TaskExecutionRepository taskExecutionRepository;
    @Autowired
    private CheckService checkService;

    @AfterEach
    public void deleteAllAfter() {
        productRepository.deleteAll();
        taskExecutionRepository.deleteAll();
    }

    @BeforeEach
    public void deleteAllBefore() {
        productRepository.deleteAll();
        taskExecutionRepository.deleteAll();
    }

    @BeforeEach
    public void init() {
        saveNewProducts.clear();
        saveNewProducts.add(new SaveNewProduct(MarketCheckType.YANDEX, "103797360000", "https://market.yandex.ru/cc/7aVTbX"));
        saveNewProducts.add(new SaveNewProduct(MarketCheckType.YANDEX, "4514870086", "https://market.yandex.ru/cc/7cDixu"));
        saveNewProducts.add(new SaveNewProduct(MarketCheckType.YANDEX, "103577166537", "https://market.yandex.ru/cc/7bbmmd"));
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
    public void startTest() throws InterruptedException {
        saveNewProducts.forEach(product -> checkService.saveNew(product));

        checkService.start();

        List<Product> productListAfterCheck = productRepository.findAll();
        productListAfterCheck.forEach(product -> {
            assertNotNull(product.getCheckTime());
        });
    }
}
