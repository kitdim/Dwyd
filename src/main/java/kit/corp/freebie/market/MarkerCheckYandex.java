package kit.corp.freebie.market;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import kit.corp.freebie.MarketCheck;
import kit.corp.model.Product;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.List;

@Component
public class MarkerCheckYandex implements MarketCheck {
    private static final String URL = "https://market.yandex.ru/pr/";
    private static final List<String> CSS_QUERY = List.of(
            "div[data-apiary-widget-id='/content/page/fancyPage/defaultPage/verifiedBadge']",
            "noframes[data-apiary='patch']");
    private String article;

    @Override
    public Document fetch(String article) {
        this.article = article;
        try {
            return Jsoup.connect(URL.concat(this.article)).get();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public JsonNode extract(Document extractValue) {
        for (String val : CSS_QUERY) {
            Elements verifiedBadges = extractValue.select(val);
            for (var elem : verifiedBadges) {
                String needBlock = elem.text();
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
        double priceWithCard = node.findValue("prices").findValue("greenPrice").findValue("price").findValue("value").asDouble();
        double priceWithoutCard = node.findValue("prices").findValue("price").findValue("value").asDouble();

        product.setPrice(priceWithoutCard);
        product.setLastPrice(priceWithoutCard);
        product.setPriceWithDiscount(priceWithCard);
        product.setCheckTime(new Timestamp(System.currentTimeMillis()));

        return product;
    }
}
