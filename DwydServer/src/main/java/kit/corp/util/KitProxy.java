package kit.corp.util;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;
import com.microsoft.playwright.options.WaitUntilState;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.stereotype.Component;

import java.text.MessageFormat;

import java.util.ArrayList;
import java.util.List;

@Deprecated
public class KitProxy {
    private static final String USER_AGENT = System.getProperty("os.name").toLowerCase().contains("linux")
            ? "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36..."
            : "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36...";

    private static final String[] URLS = new String[]{
            "https://www.ozon.ru",
            "https://www.ozon.ru/product/{0}",
            "https://www.wildberries.ru",
            "https://www.wildberries.ru/catalog/{0}/detail.aspx"};


    private static final Browser.NewContextOptions CONTEXT_OPTIONS = new Browser.NewContextOptions()
            .setUserAgent(USER_AGENT)
            .setViewportSize(1600, 900)
            .setBypassCSP(true)
            .setIgnoreHTTPSErrors(true);

    public static Document executeBrowserAutomation(String article, String marketName) {
        boolean isOzon = marketName.equals("OZON");
        boolean isWb = marketName.equals("WB");

        try (Playwright playwright = Playwright.create()) {
            Browser browser = playwright.chromium().launch(getBrowserOptions());
            BrowserContext context = browser.newContext(CONTEXT_OPTIONS);
            Page page = context.newPage();
            page.addInitScript("Object.defineProperty(navigator, 'webdriver', {get: () => undefined})");

            if (isWb) {
                executeNavigateToMainPage(page);
                humanLikeInteraction(page);
            }
            String productUrl = isOzon ?
                    MessageFormat.format(URLS[1], article) :
                    MessageFormat.format(URLS[3], article);

            executeNavigateToProductPage(page, productUrl);

            return Jsoup.parse(page.content());
        }
    }

    private static void executeNavigateToMainPage(Page page) {
        page.navigate(URLS[2], new Page.NavigateOptions().setWaitUntil(WaitUntilState.DOMCONTENTLOADED));
    }

    private static void executeNavigateToProductPage(Page page, String productUrl) {
        page.navigate(productUrl, new Page.NavigateOptions().setWaitUntil(WaitUntilState.NETWORKIDLE));
    }

    private static void humanLikeInteraction(Page page) {
        page.waitForTimeout(2000 + (long) (Math.random() * 3000));
        page.mouse().move(100, 200);
        page.waitForTimeout(1000 + (long) (Math.random() * 2000));
        page.mouse().wheel(0, 300);
        page.waitForTimeout(1500);
    }

    private static BrowserType.LaunchOptions getBrowserOptions() {
        List<String> args = new ArrayList<>();
        String os = System.getProperty("os.name").toLowerCase();
        boolean isWindows = os.contains("win");
        boolean isLinux = os.contains("linux");

        args.add("--disable-blink-features=AutomationControlled");
        args.add("--disable-infobars");
        args.add("--disable-breakpad");

        if (isLinux) {
            args.add("--no-sandbox");
            args.add("--disable-dev-shm-usage");
            args.add("--disable-gpu");
        } else if (isWindows) {
            args.add("--disable-features=RendererCodeIntegrity");
            args.add("--disable-background-networking");
            args.add("--disable-component-update");
            args.add("--disable-default-apps");
            args.add("--disable-extensions");
            args.add("--no-first-run");
            args.add("--disable-notifications");
        }

        return new BrowserType.LaunchOptions()
                .setHeadless(true)
                .setArgs(args)
                .setChromiumSandbox(isLinux);
    }
}
