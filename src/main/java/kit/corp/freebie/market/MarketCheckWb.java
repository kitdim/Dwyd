package kit.corp.freebie.market;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import kit.corp.freebie.MarketCheck;
import kit.corp.freebie.MarketCheckType;
import kit.corp.model.Product;
import kit.corp.util.KitProxy;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.util.List;
import java.util.Objects;

@Component
public class MarketCheckWb implements MarketCheck {
    private static final List<String> CSS_QUERY = List.of(
            ".price-block__content"
    );
    private String article;

    @Override
    public Document fetch(String article) {
        this.article = article;
        return KitProxy.executeBrowserAutomation(article, MarketCheckType.WB.name());
    }

    @Override
    public JsonNode extract(Document extractValue) {
        JsonNode jsonNode = null;
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode jsonResult = mapper.createObjectNode();

        for (String val : CSS_QUERY) {
            Elements verifiedBadge = extractValue.select(val);
            if (verifiedBadge.isEmpty()) {
                throw new RuntimeException();
            }
            for (Element element : verifiedBadge) {
                if (jsonResult.isEmpty()) {
                    try {
                        String finalPrice = Objects.requireNonNull(element
                                        .selectFirst(".price-block__final-price"))
                                .text();
                        String walletPrice = Objects.requireNonNull(element
                                        .selectFirst(".price-block__wallet-price"))
                                .text();

                        jsonResult.put("finalPrice", finalPrice);
                        jsonResult.put("walletPrice", walletPrice);
                    } catch (NullPointerException exception) {
                        throw new NullPointerException();
                    }
                } else {
                    try {
                        String jsonOutput = mapper
                                .writerWithDefaultPrettyPrinter()
                                .writeValueAsString(jsonResult);
                        jsonNode = mapper.readTree(jsonOutput);

                        break;
                    } catch (JsonProcessingException e) {
                        throw new RuntimeException();
                    }
                }

            }
        }
        if (jsonNode == null) {
            throw new NullPointerException();
        }
        return jsonNode;
    }

    @Override
    public Product getPrice(JsonNode node) {
        Product product = new Product();

        double greenPrice = Double.parseDouble(node.get("finalPrice").asText().replaceAll("[^0-9]", ""));
        double priceWithDiscount = Double.parseDouble(node.get("walletPrice").asText().replaceAll("[^0-9]", ""));

        product.setPrice(priceWithDiscount);
        product.setPriceWithDiscount(greenPrice);
        product.setLastPrice(priceWithDiscount);
        product.setCheckTime(new Timestamp(System.currentTimeMillis()));

        return product;
    }
}
