package kit.corp.service;

import com.fasterxml.jackson.databind.JsonNode;
import kit.corp.freebie.MarketCheck;
import kit.corp.freebie.MarketCheckType;
import kit.corp.freebie.market.MarkerCheckYandex;
import kit.corp.freebie.market.MarketCheckOzon;
import kit.corp.freebie.market.MarketCheckWb;
import kit.corp.model.Product;
import kit.corp.repository.impl.ProductRepositoryInMemory;
import org.jsoup.nodes.Document;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class CheckService {
    private static final ProductRepositoryInMemory productRepositoryInMemory = new ProductRepositoryInMemory();

    public static void start() {
        if (!productRepositoryInMemory.isEmpty()) {
            List<Product> products = productRepositoryInMemory.getAll();

            for (Product product : products) {
                MarketCheck check = getMarketCheck(product.getMarket());
                Document document = check.fetch(product.getArticle());
                JsonNode jsonNode = check.extract(document);
                Product checkProduct = check.getPrice(jsonNode);

                Product checkPriceProduct = checkPrice(product, checkProduct);
                productRepositoryInMemory.edit(checkPriceProduct);
            }
        } else {
            System.out.println("Нечего проверять");
        }
    }

    public void saveNew(MarketCheckType type, String article) {
        Product product = new Product();
        product.setMarket(type);
        product.setArticle(article);

        productRepositoryInMemory.save(product);
    }

    private static MarketCheck getMarketCheck(MarketCheckType type) {
        MarketCheck marketCheck;
        switch (type) {
            case YANDEX -> marketCheck = new MarkerCheckYandex();
            case OZON -> marketCheck = new MarketCheckOzon();
            case WB -> marketCheck = new MarketCheckWb();
            default -> throw new IllegalStateException("Unexpected value: " + type);
        }
        return marketCheck;
    }

    private static Product checkPrice(Product product, Product checkProduct) {
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
}
