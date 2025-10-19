package kit.corp.model.product.dto;

import jakarta.validation.constraints.NotNull;
import kit.corp.freebie.MarketCheckType;
import kit.corp.handler.exception.MarketIsNotSupportException;

public record SaveNewProduct(@NotNull(message = "Название магазина не должно быть пустым")
                             MarketCheckType marketCheckType,
                             @NotNull(message = "Артикл не должен быть пустым")
                             String article,
                             @NotNull(message = "Ссылка на товар не должна быть пустой")
                             String shortLink) {

    public SaveNewProduct {
        if (marketCheckType != MarketCheckType.YANDEX) {
            throw new MarketIsNotSupportException("Only YANDEX market check type is allowed");
        }
    }
}
