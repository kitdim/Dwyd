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
import java.util.Objects;

@RequiredArgsConstructor
public class MarkerCheckYandex implements MarketCheck {
    private static final String URL = "https://market.yandex.ru/cc/";
    private static final List<String> CSS_QUERY = List.of(
            "noframes[data-apiary='patch']",
            "div[data-apiary-widget-id='/content/page/fancyPage/defaultPage/verifiedBadge']");
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    private final String article;
    private final String shortLink;

    @Override
    public Document fetch() {
        try {
            return Jsoup.connect(shortLink).get();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public JsonNode extract(final Document extractValue) {
        return CSS_QUERY.parallelStream()
                .flatMap(val -> {
                    Elements verifiedBadges = extractValue.select(val);
                    return verifiedBadges.parallelStream();
                })
                .map(elem -> {
                    String needBlock = elem.text();
                    if (needBlock.contains(article) && needBlock.contains("price")) {
                        try {
                            return OBJECT_MAPPER.readTree(needBlock);
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                    }
                    return null;
                })
                .filter(Objects::nonNull)
                .findAny()
                .orElse(null);
    }

    @Override
    public Product getPrice(final JsonNode node) {
        Product product = new Product();
        double priceWithoutCard = node.findValue("price")
                .findValue("value")
                .asDouble();

        product.setPrice(priceWithoutCard);
        product.setLastPrice(priceWithoutCard);
        product.setPriceWithDiscount(priceWithoutCard);
        product.setCheckTime(new Timestamp(System.currentTimeMillis()));

        return product;
    }
}
