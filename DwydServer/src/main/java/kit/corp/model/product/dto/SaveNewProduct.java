package kit.corp.model.product.dto;

import jakarta.validation.constraints.NotNull;
import kit.corp.freebie.MarketCheckType;

public record SaveNewProduct(@NotNull(message = "Не указано название магазина")
                             MarketCheckType marketCheckType,
                             @NotNull(message = "Артикл не указан")
                             String article,
                             @NotNull(message = "Ссылка не указана")
                             String shortLink) {
}
