package kit.corp.controller.api;

import jakarta.validation.Valid;
import kit.corp.controller.api.response.ApiResponse;
import kit.corp.controller.api.response.ApiResponseWithObject;
import kit.corp.model.product.dto.SaveNewProduct;
import kit.corp.service.CheckService;
import kit.corp.service.PriceNotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/marketCheck/v1")
@RequiredArgsConstructor
@Slf4j
public class MarketCheckController {
    private final CheckService checkService;
    private final PriceNotificationService priceNotificationService;

    @PostMapping("/start")
    public ResponseEntity<ApiResponse> start() {
        log.info("called api/marketCheck/v1/start");
        checkService.start();

        return ResponseEntity.accepted().body(new ApiResponse("Обработка запущена", true));
    }

    @PostMapping("/save")
    public ResponseEntity<ApiResponse> save(@Valid @RequestBody final SaveNewProduct saveProduct) {
        log.info("called api/marketCheck/v1/save");
        checkService.saveNew(saveProduct);

        return ResponseEntity.ok(new ApiResponse("Товар сохранён", true));
    }

    @GetMapping("/notification/{id}")
    public ResponseEntity<ApiResponseWithObject> getNotification(@PathVariable Long id) {
        log.info("called api/marketCheck/v1/getNotification");

        return ResponseEntity.ok(new ApiResponseWithObject(priceNotificationService.getPriceNotificationsByUserId(id), true));
    }
}
