package kit.corp;

import kit.corp.freebie.MarketCheckType;
import kit.corp.service.CheckService;

public class Main {

    public static void main(String[] args) {
        CheckService service = new CheckService();
        service.saveNew(MarketCheckType.OZON, "1685279844");
        service.saveNew(MarketCheckType.YANDEX, "4801668634");
        service.saveNew(MarketCheckType.WB, "176047927");

        CheckService.start();
    }
}
