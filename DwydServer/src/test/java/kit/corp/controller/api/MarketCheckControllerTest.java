package kit.corp.controller.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import kit.corp.freebie.MarketCheckType;
import kit.corp.model.product.dto.SaveNewProduct;
import kit.corp.repository.ProductRepository;
import org.instancio.Instancio;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import java.util.stream.Stream;

import static org.instancio.Select.field;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class MarketCheckControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper om;
    @Autowired
    private ProductRepository productRepository;

    @BeforeEach
    public void init() {
        productRepository.deleteAll();
    }

    @ParameterizedTest
    @MethodSource("provideTestGoodData")
    public void startTestStatusIsOk(final MarketCheckType marketCheckType, final String article, final String shortLink) throws Exception {
        SaveNewProduct newProduct = Instancio.of(SaveNewProduct.class)
                .set(field("article"), article)
                .set(field("marketCheckType"), marketCheckType)
                .set(field("shortLink"), shortLink)
                .create();

        MockHttpServletRequestBuilder requestSave = post("/api/marketCheck/v1/save")
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(newProduct));

        mockMvc.perform(requestSave);

        MockHttpServletRequestBuilder requestStart = post("/api/marketCheck/v1/start")
                .contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(requestStart)
                .andExpect(status().isAccepted());
    }

    @ParameterizedTest
    @MethodSource("provideTestGoodData")
    public void saveTestStatusIsOk(final MarketCheckType marketCheckType, final String article, final String shortLink) throws Exception {
        SaveNewProduct newProduct = Instancio.of(SaveNewProduct.class)
                .set(field("article"), article)
                .set(field("marketCheckType"), marketCheckType)
                .set(field("shortLink"), shortLink)
                .create();

        MockHttpServletRequestBuilder request = post("/api/marketCheck/v1/save")
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(newProduct));

        mockMvc.perform(request)
                .andExpect(status().isOk());
    }

    public static Stream<Arguments> provideTestGoodData() {
        return Stream.of(
                Arguments.of(MarketCheckType.OZON, "856552942", ""),
                Arguments.of(MarketCheckType.YANDEX, "103797360000", "https://market.yandex.ru/cc/7aVTbX"),
                Arguments.of(MarketCheckType.WB, "239109987", "")
        );
    }
}
