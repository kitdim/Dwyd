package kit.corp.freebie.market;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import kit.corp.freebie.MarketCheck;
import kit.corp.model.product.Product;
import lombok.RequiredArgsConstructor;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.List;

@RequiredArgsConstructor
public class MarkerCheckYandex implements MarketCheck {
    private static final String URL = "https://market.yandex.ru/pr/";
    private static final List<String> CSS_QUERY = List.of(
            "div[data-apiary-widget-id='/content/page/fancyPage/defaultPage/verifiedBadge']",
            "noframes[data-apiary='patch']");
    private final String article;

    @Override
    public Document fetch() {
        try {
            return Jsoup.connect(URL.concat(this.article)).get();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public JsonNode extract(final Document extractValue) {
        for (String val : CSS_QUERY) {
            Elements verifiedBadges = extractValue.select(val);
            for (var elem : verifiedBadges) {
                String needBlock = elem.text();
                if (needBlock.contains("gci") && needBlock.contains(article)) {
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
        double priceWithCard = node.findValue("prices")
                .findValue("greenPrice")
                .findValue("price")
                .findValue("value")
                .asDouble();
        double priceWithoutCard = node.findValue("prices")
                .findValue("price")
                .findValue("value")
                .asDouble();

        product.setPrice(priceWithoutCard);
        product.setLastPrice(priceWithoutCard);
        product.setPriceWithDiscount(priceWithCard);
        product.setCheckTime(new Timestamp(System.currentTimeMillis()));

        return product;
    }
}
