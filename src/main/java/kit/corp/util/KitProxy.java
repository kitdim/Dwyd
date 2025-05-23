package kit.corp.util;

import com.microsoft.playwright.*;
import com.microsoft.playwright.options.WaitUntilState;
import kit.corp.freebie.MarketCheckType;
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

    private static final BrowserType.LaunchOptions BROWSER_OPTIONS = new BrowserType.LaunchOptions()
            .setHeadless(true)
            .setArgs(Arrays.asList(
                    "--disable-blink-features=AutomationControlled",
                    "--no-sandbox",
                    "--disable-dev-shm-usage",
                    "--single-process"
            ));

    private static final Browser.NewContextOptions CONTEXT_OPTIONS = new Browser.NewContextOptions()
            .setUserAgent("Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/119.0.0.0 Safari/537.36")
            .setViewportSize(1600, 900)
            .setBypassCSP(true)
            .setIgnoreHTTPSErrors(true);

    public static Document executeBrowserAutomation(String article, String marketName) {
        try (Playwright playwright = Playwright.create()) {
            Browser browser = playwright.chromium().launch(BROWSER_OPTIONS);
            BrowserContext context = browser.newContext(CONTEXT_OPTIONS);

            Page page = context.newPage();
            page.addInitScript("Object.defineProperty(navigator, 'webdriver', {get: () => undefined})");

            executeNavigateToMainPage(page, marketName);
            humanLikeInteraction(page);

            String productUrl = marketName.equals("OZON") ?
                    MessageFormat.format(URLS[1], article) :
                    MessageFormat.format(URLS[3], article);

            executeNavigateToProductPage(page, productUrl, marketName);

            return Jsoup.parse(page.content());
        }
    }

    private static void executeNavigateToMainPage(Page page, String marketName) {
        if (marketName.equals("WB")) {
            page.navigate(
                    URLS[2],
                    new Page.NavigateOptions().setWaitUntil(WaitUntilState.DOMCONTENTLOADED)
            );
        }
    }

    private static void executeNavigateToProductPage(Page page, String productUrl, String marketName) {
        page.navigate(productUrl, new Page.NavigateOptions()
                .setWaitUntil(WaitUntilState.NETWORKIDLE));
    }

    private static void humanLikeInteraction(Page page) {
        page.waitForTimeout(2000 + (long) (Math.random() * 3000));
        page.mouse().move(100, 200);
        page.waitForTimeout(1000 + (long) (Math.random() * 2000));
        page.mouse().wheel(0, 300);
        page.waitForTimeout(1500);
    }
}
