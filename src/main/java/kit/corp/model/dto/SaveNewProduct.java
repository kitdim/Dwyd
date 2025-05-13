package kit.corp.model.dto;

import jakarta.validation.constraints.NotNull;
import kit.corp.freebie.MarketCheckType;

public record SaveNewProduct(@NotNull MarketCheckType marketCheckType,
                             @NotNull String article) {
}
