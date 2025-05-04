package kit.corp.freebie;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import kit.corp.model.Product;
import org.jsoup.nodes.Document;

import java.io.IOException;

public interface MarketCheck {
    Document fetch(String article);
    JsonNode extract(Document extractValue);
    Product getPrice(JsonNode node);
}
