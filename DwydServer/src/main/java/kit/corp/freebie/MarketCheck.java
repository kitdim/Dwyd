package kit.corp.freebie;

import com.fasterxml.jackson.databind.JsonNode;
import kit.corp.model.product.Product;
import org.jsoup.nodes.Document;

public interface MarketCheck {
    Document fetch();
    JsonNode extract(Document extractValue);
    Product getPrice(JsonNode node);
}
