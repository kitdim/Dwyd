package kit.corp.freebie.market;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import kit.corp.freebie.MarketCheck;
import kit.corp.freebie.MarketCheckType;
import kit.corp.model.Product;
import kit.corp.util.KitProxy;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.util.List;

@Component
public class MarketCheckOzon implements MarketCheck {
    private static final List<String> CSS_QUERY = List.of(
            "script[type='application/ld+json']"
    );
    private String article;

    @Override
    public Document fetch(String article) {
        this.article = article;
        return KitProxy.executeBrowserAutomation(article, MarketCheckType.OZON.name());
    }

    @Override
    public JsonNode extract(Document extractValue) {
        for (String val : CSS_QUERY) {
            Elements verifiedBadges = extractValue.select(val);
            for (var elem : verifiedBadges) {
                String needBlock = elem.data();
                if (needBlock.contains(article)) {
                    ObjectMapper objectMapper = new ObjectMapper();
                    try {
                        return objectMapper.readTree(needBlock);
                    } catch (JsonProcessingException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }
        return null;
    }

    @Override
    public Product getPrice(JsonNode node) {
        Product product = new Product();
        double priceWithDiscount = node.path("offers").path("price").asDouble();

        product.setPrice(priceWithDiscount);
        product.setLastPrice(priceWithDiscount);
        product.setPriceWithDiscount(priceWithDiscount);
        product.setCheckTime(new Timestamp(System.currentTimeMillis()));

        return product;
    }
}
