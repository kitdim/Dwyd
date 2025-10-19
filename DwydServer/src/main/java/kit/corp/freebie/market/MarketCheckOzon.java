package kit.corp.freebie.market;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import kit.corp.freebie.MarketCheck;
import kit.corp.model.product.Product;
import lombok.RequiredArgsConstructor;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.sql.Timestamp;
import java.util.List;

@RequiredArgsConstructor
@Deprecated
public class MarketCheckOzon implements MarketCheck {
    private static final List<String> CSS_QUERY = List.of(
            "script[type='application/ld+json']"
    );
    private final String article;


    @Override
    public Document fetch() {
        throw new IllegalArgumentException("Not support.");
    }

    @Override
    public JsonNode extract(final Document extractValue) {
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
    public Product getPrice(final JsonNode node) {
        Product product = new Product();
        double priceWithDiscount = node.path("offers").path("price").asDouble();

        product.setPrice(priceWithDiscount);
        product.setLastPrice(priceWithDiscount);
        product.setPriceWithDiscount(priceWithDiscount);
        product.setCheckTime(new Timestamp(System.currentTimeMillis()));

        return product;
    }
}
