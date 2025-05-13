package kit.corp.service;

import com.fasterxml.jackson.databind.JsonNode;
import kit.corp.freebie.MarketCheck;
import kit.corp.freebie.MarketCheckType;
import kit.corp.freebie.market.MarkerCheckYandex;
import kit.corp.freebie.market.MarketCheckOzon;
import kit.corp.freebie.market.MarketCheckWb;
import kit.corp.model.Product;
import kit.corp.model.dto.SaveNewProduct;
import kit.corp.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.jsoup.nodes.Document;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CheckService {
    private final ProductRepository productRepository;

    public void start() {
        if (productRepository.count() > 0) {
            List<Product> products = productRepository.findAll();

            for (Product product : products) {
                MarketCheck check = getMarketCheck(product.getMarket());
                Document document = check.fetch(product.getArticle());
                JsonNode jsonNode = check.extract(document);
                Product checkProduct = check.getPrice(jsonNode);

                Product checkPriceProduct = checkPrice(product, checkProduct);
                edit(checkPriceProduct);
            }
        } else {
            System.out.println("Нечего проверять");
        }
    }

    public void saveNew(SaveNewProduct saveNewProduct) {
        Product product = new Product();
        product.setMarket(saveNewProduct.marketCheckType());
        product.setArticle(saveNewProduct.article());

        productRepository.save(product);
    }

    private MarketCheck getMarketCheck(MarketCheckType type) {
        MarketCheck marketCheck;
        switch (type) {
            case YANDEX -> marketCheck = new MarkerCheckYandex();
            case OZON -> marketCheck = new MarketCheckOzon();
            case WB -> marketCheck = new MarketCheckWb();
            default -> throw new IllegalStateException("Unexpected value: " + type);
        }
        return marketCheck;
    }

    private Product checkPrice(Product product, Product checkProduct) {
        String currentTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        if (product.getCheckTime() == null) {
            product.setCheckTime(checkProduct.getCheckTime());
            product.setLastPrice(checkProduct.getLastPrice());
            product.setPriceWithDiscount(checkProduct.getPriceWithDiscount());
            product.setPrice(checkProduct.getPrice());

            System.out.println("Текущее время: " + currentTime);
            System.out.println(product);

            return product;
        }
        if (product.getPrice() > checkProduct.getPrice()) {
            product.setCheckTime(checkProduct.getCheckTime());
            product.setPrice(checkProduct.getPrice());
            product.setLastPrice(checkProduct.getLastPrice());
            product.setPriceWithDiscount(checkProduct.getPriceWithDiscount());

            System.out.println("Текущее время: " + currentTime);
            System.out.println(product + " цена поменялась.");

            return product;
        } else {
            product.setCheckTime(checkProduct.getCheckTime());
            System.out.println("Текущее время: " + currentTime);
            System.out.println(product + " цена не поменялась.");

            return product;
        }
    }

    private void edit(Product checkPriceProduct) {
        Product productFromDb = productRepository.findById(checkPriceProduct.getId())
                .orElseThrow(() -> new RuntimeException("Not Found: " + checkPriceProduct.getId()));

        productFromDb.setPrice(checkPriceProduct.getPrice());
        productFromDb.setPriceWithDiscount(checkPriceProduct.getPriceWithDiscount());
        productFromDb.setLastPrice(checkPriceProduct.getLastPrice());
        productFromDb.setCheckTime(checkPriceProduct.getCheckTime());

        productRepository.save(productFromDb);
    }
}
