package kit.corp.controller.api;

import jakarta.validation.Valid;
import kit.corp.model.product.dto.SaveNewProduct;
import kit.corp.service.CheckService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/marketCheck/v1")
@RequiredArgsConstructor
@Slf4j
public class MarketCheckController {
    private final CheckService checkService;

    @PostMapping("/start")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void start() {
        log.info("called api/marketCheck/v1/start");
        checkService.start();
    }

    @PostMapping("/save")
    @ResponseStatus(HttpStatus.OK)
    public void save(@Valid @RequestBody final SaveNewProduct saveProduct) {
        log.info("called api/marketCheck/v1/save");
        checkService.saveNew(saveProduct);
    }
}
