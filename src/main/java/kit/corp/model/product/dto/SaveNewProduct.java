package kit.corp.model.product.dto;

import jakarta.validation.constraints.NotNull;
import kit.corp.freebie.MarketCheckType;

public record SaveNewProduct(@NotNull MarketCheckType marketCheckType,
                             @NotNull String article,
                             String shortLink) {
}
