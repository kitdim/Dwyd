package kit.corp.controller.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import kit.corp.freebie.MarketCheckType;
import kit.corp.model.product.dto.SaveNewProduct;
import org.instancio.Instancio;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import java.util.stream.Stream;

import static org.instancio.Select.field;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class MarketCheckControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper om;

    @Test()
    @DisplayName("Test by start method")
    public void startTestStatusIsOk() throws Exception {
        MockHttpServletRequestBuilder requestStart = post("/api/marketCheck/v1/start")
                .contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(requestStart)
                .andExpect(status().isAccepted());
    }

    @Test()
    @DisplayName("Test by start method (response)")
    public void responseByStartMethodIsTrueAndIsEqualsTest() throws Exception {
        MvcResult mvcResult = mockMvc.perform(post("/api/marketCheck/v1/start")).andReturn();

        ApiResponse apiResponse = om.readValue(mvcResult.getResponse().getContentAsString(), ApiResponse.class);

        assertTrue(apiResponse.isSuccess());
        assertEquals("Обработка запущена", apiResponse.message());
    }

    @ParameterizedTest(name = "Test by save method with data: {0} {1} {2}")
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

    @ParameterizedTest(name = "Test by save method(response) with data: {0} {1} {2}")
    @MethodSource("provideTestGoodData")
    public void responseBySaveIsTrueAndIsEqualsTest(final MarketCheckType marketCheckType, final String article, final String shortLink) throws Exception {
        SaveNewProduct newProduct = Instancio.of(SaveNewProduct.class)
                .set(field("article"), article)
                .set(field("marketCheckType"), marketCheckType)
                .set(field("shortLink"), shortLink)
                .create();

        MvcResult mvcResult = mockMvc.perform(post("/api/marketCheck/v1/save")
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(newProduct))
        ).andReturn();

        ApiResponse apiResponse = om.readValue(mvcResult.getResponse().getContentAsString(), ApiResponse.class);

        assertTrue(apiResponse.isSuccess());
        assertEquals("Товар сохранён", apiResponse.message());
    }

    @ParameterizedTest(name = "Test by save method(response) with bad data: {0} {1} {2}")
    @MethodSource("provideTestBadDataByUnprocessMarkets")
    public void responseStatusByUnprocessMarkets(final MarketCheckType marketCheckType, final String article, final String shortLink) throws Exception {
        SaveNewProduct newProduct = Instancio.of(SaveNewProduct.class)
                .set(field("article"), article)
                .set(field("marketCheckType"), marketCheckType)
                .set(field("shortLink"), shortLink)
                .create();

        MockHttpServletRequestBuilder request = post("/api/marketCheck/v1/save")
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(newProduct));

        mockMvc.perform(request)
                .andExpect(status().isBadRequest());
    }


    public static Stream<Arguments> provideTestGoodData() {
        return Stream.of(
                Arguments.of(MarketCheckType.YANDEX, "103797360000", "https://market.yandex.ru/cc/7aVTbX"),
                Arguments.of(MarketCheckType.YANDEX, "4514870086", "https://market.yandex.ru/cc/7cDixu"),
                Arguments.of(MarketCheckType.YANDEX, "103577166537", "https://market.yandex.ru/cc/7bbmmd"));
    }

    public static Stream<Arguments> provideTestBadDataByUnprocessMarkets() {
        return Stream.of(
                Arguments.of(MarketCheckType.WB, "405801795", "https://www.wildberries.ru/catalog/405801795/detail.aspx?targetUrl=SN"),
                Arguments.of(MarketCheckType.OZON, "1744752854", "https://ozon.ru/t/QoxQ2es"));
    }
}
