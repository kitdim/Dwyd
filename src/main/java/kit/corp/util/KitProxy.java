package kit.corp.util;

import com.microsoft.playwright.*;
import com.microsoft.playwright.options.WaitUntilState;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.stereotype.Component;

import java.text.MessageFormat;

import java.util.Arrays;

@Component
public class KitProxy {
    private static final String[] URLS = new String[]{
            "https://www.ozon.ru",
            "https://www.ozon.ru/product/{0}",
            "https://www.wildberries.ru",
            "https://www.wildberries.ru/catalog/{0}/detail.aspx"};

    public static Document executeBrowserAutomation(String article, String marketName) {
        try (Playwright playwright = Playwright.create()) {
            Browser browser = playwright.chromium().launch(new BrowserType.LaunchOptions()
                    .setHeadless(false)
                    .setArgs(Arrays.asList(
                            "--disable-blink-features=AutomationControlled",
                            "--start-maximized",
                            "--no-sandbox",
                            "--disable-dev-shm-usage"
                    )));

            BrowserContext context = browser.newContext(new Browser.NewContextOptions()
                    .setUserAgent("Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/119.0.0.0 Safari/537.36;")
                    .setViewportSize(1600, 900)
                    .setLocale("ru-RU")
                    .setGeolocation(55.735537, 37.672553)
                    .setTimezoneId("Europe/Moscow")
                    .setBypassCSP(true)
                    .setIgnoreHTTPSErrors(true));

            Page page = context.newPage();
            page.addInitScript("Object.defineProperty(navigator, 'webdriver', {get: () => undefined})");

            page.navigate(
                    marketName.equals("OZON") ? URLS[0] : URLS[2],
                    new Page.NavigateOptions().setWaitUntil(WaitUntilState.NETWORKIDLE)
            );

            humanLikeInteraction(page);

            String productUrl = marketName.equals("OZON") ?
                    MessageFormat.format(URLS[1], article) :
                    MessageFormat.format(URLS[3], article);

            page.navigate(productUrl, new Page.NavigateOptions()
                    .setWaitUntil(WaitUntilState.NETWORKIDLE));
            String html = page.content();

            return Jsoup.parse(html);
        }
    }

    private static void humanLikeInteraction(Page page) {
        page.waitForTimeout(2000 + (long) (Math.random() * 3000));
        page.mouse().move(100, 200);
        page.waitForTimeout(1000 + (long) (Math.random() * 2000));
        page.mouse().wheel(0, 300);
        page.waitForTimeout(1500);
    }
}
