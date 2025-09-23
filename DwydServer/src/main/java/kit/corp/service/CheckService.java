package kit.corp.service;

import com.fasterxml.jackson.databind.JsonNode;
import kit.corp.config.TaskConfiguration;
import kit.corp.freebie.MarketCheck;
import kit.corp.freebie.MarketCheckType;
import kit.corp.freebie.market.MarkerCheckYandex;
import kit.corp.freebie.market.MarketCheckOzon;
import kit.corp.freebie.market.MarketCheckWb;
import kit.corp.model.product.Product;
import kit.corp.model.product.ProductProcessType;
import kit.corp.model.product.dto.SaveNewProduct;
import kit.corp.model.task.TaskExecution;
import kit.corp.model.task.TaskStatus;
import kit.corp.repository.ProductRepository;
import kit.corp.repository.TaskExecutionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.nodes.Document;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Slf4j
public class CheckService {
    private final ProductRepository productRepository;
    private final TaskExecutionRepository taskExecutionRepository;
    private final TaskConfiguration taskConfiguration;

    @Scheduled(fixedDelayString = "${tasks.start.time-delay}", timeUnit = TimeUnit.MINUTES)
    public void start() {
        String taskName = taskConfiguration.getStart().getTaskName();
        if (taskExecutionRepository.isRunningTask(taskName, TaskStatus.RUNNING)) {
            log.warn("Task {} isn't completed yet.", taskName);
            return;
        }
        TaskExecution taskExecution = createTaskExecution(taskName);
        taskExecutionRepository.save(taskExecution);

        TaskResult result = processProducts();

        completeTaskExecution(taskExecution, result);
    }

    public void saveNew(final SaveNewProduct saveNewProduct) {
        Product product = new Product();
        product.setMarket(saveNewProduct.marketCheckType());
        product.setArticle(saveNewProduct.article());
        product.setShortLink(saveNewProduct.shortLink());
        product.setProductProcessType(ProductProcessType.SALE);

        productRepository.save(product);
    }

    private TaskExecution createTaskExecution(String taskName) {
        List<String> taskParams = List.of(
                String.valueOf(taskConfiguration.getStart().getTimeDelay()),
                String.valueOf(taskConfiguration.isNeedAuth())
        );
        log.info("Task {} is running with params {}.", taskName, taskParams);
        return TaskExecution.builder()
                .taskName(taskName)
                .taskStatus(TaskStatus.RUNNING)
                .startTime(new Timestamp(System.currentTimeMillis()))
                .startParams(String.join(";", taskParams))
                .build();
    }

    private TaskResult processProducts() {
        List<Product> products = productRepository.findAllProductsByProcessType(ProductProcessType.SALE);
        long countUpdateElems = 0;
        long countErrors = 0;

        if (!products.isEmpty()) {
            for (Product product : products) {
                try {
                    MarketCheck check = getMarketCheck(product.getMarket(), product.getArticle(), product.getShortLink());
                    Document document = check.fetch();
                    JsonNode jsonNode = check.extract(document);
                    Product checkProduct = check.getPrice(jsonNode);

                    Product updatedProduct = checkPrice(product, checkProduct);
                    edit(updatedProduct);
                    countUpdateElems++;

                    log.debug("Processed by product: {}", updatedProduct);
                } catch (Exception e) {
                    product.setProductProcessType(ProductProcessType.NOT_FOUND);
                    productRepository.save(product);
                    countErrors++;

                    log.error("{} with error: {}.\nStack trace:", product.getArticle(), e.getMessage(), e);
                }
            }
        } else {
            log.warn("Nothing to check.");
        }

        long countElems = products.size();
        return new TaskResult(countElems, countUpdateElems, countErrors);
    }

    private void completeTaskExecution(TaskExecution taskExecution, TaskResult result) {
        String description = String.format(
                "Count elems: %d%nCount error: %d%nCount update: %d%n",
                result.total(), result.errors(), result.updates()
        );

        TaskStatus status = result.errors() > 0 ? TaskStatus.ERROR : TaskStatus.FINISH;

        taskExecution.setFinishTime(new Timestamp(System.currentTimeMillis()));
        taskExecution.setDescription(description);
        taskExecution.setTaskStatus(status);

        log.info(description);
        taskExecutionRepository.save(taskExecution);
    }

    private MarketCheck getMarketCheck(final MarketCheckType type, final String article, final String shortLink) {
        return switch (type) {
            case YANDEX -> new MarkerCheckYandex(article, shortLink);
            default -> throw new IllegalArgumentException(type + " is not support.");
        };
    }

    private Product checkPrice(final Product product, final Product checkProduct) {
        String currentTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        if (product.getCheckTime() == null) {
            product.setCheckTime(checkProduct.getCheckTime());
            product.setLastPrice(checkProduct.getLastPrice());
            product.setPriceWithDiscount(checkProduct.getPriceWithDiscount());
            product.setPrice(checkProduct.getPrice());

            log.info("Time: {}", currentTime);
            log.debug("Check product: {}", product);

            return product;
        }
        if (product.getPrice() > checkProduct.getPrice()) {
            product.setCheckTime(checkProduct.getCheckTime());
            product.setPrice(checkProduct.getPrice());
            product.setLastPrice(checkProduct.getLastPrice());
            product.setPriceWithDiscount(checkProduct.getPriceWithDiscount());
            log.debug("Check product: {}, price update.", product);

            return product;
        } else {
            product.setCheckTime(checkProduct.getCheckTime());
            log.debug("Check product: {}, price not update.", product);

            return product;
        }
    }

    private void edit(final Product checkPriceProduct) {
        Product productFromDb = productRepository.findById(checkPriceProduct.getId())
                .orElseThrow(() -> new RuntimeException("Not Found: " + checkPriceProduct.getId()));

        productFromDb.setPrice(checkPriceProduct.getPrice());
        productFromDb.setPriceWithDiscount(checkPriceProduct.getPriceWithDiscount());
        productFromDb.setLastPrice(checkPriceProduct.getLastPrice());
        productFromDb.setCheckTime(checkPriceProduct.getCheckTime());

        productRepository.save(productFromDb);
        log.debug("Save product: {}.", productFromDb);
    }

    private record TaskResult(long total, long updates, long errors) {}
}
