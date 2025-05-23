package kit.corp.util;

import kit.corp.freebie.MarketCheckType;
import org.junit.jupiter.api.Timeout;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.stream.Stream;


@SpringBootTest
class KitProxyTest {

    @ParameterizedTest
    @MethodSource("provideTestGoodData")
    @Timeout(15)
    public void executeBrowserAutomation(MarketCheckType marketCheckType, String article) {
        KitProxy.executeBrowserAutomation(article, marketCheckType.name());
    }

    public static Stream<Arguments> provideTestGoodData() {
        return Stream.of(
                Arguments.of(MarketCheckType.OZON, "856552942"),
                Arguments.of(MarketCheckType.WB, "239109987")
        );
    }
}