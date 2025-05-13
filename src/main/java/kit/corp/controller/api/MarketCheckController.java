package kit.corp.controller.api;

import jakarta.validation.Valid;
import kit.corp.model.dto.SaveNewProduct;
import kit.corp.service.CheckService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/marketCheck/v1")
@RequiredArgsConstructor
public class MarketCheckController {
    private final CheckService checkService;

    @PostMapping("/start")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void start() {
        checkService.start();
    }

    @PostMapping("/save")
    @ResponseStatus(HttpStatus.OK)
    public void save(@Valid @RequestBody SaveNewProduct saveProduct) {
        checkService.saveNew(saveProduct);
    }
}
