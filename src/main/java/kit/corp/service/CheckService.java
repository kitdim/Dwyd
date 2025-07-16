package kit.corp.service;

import com.fasterxml.jackson.databind.JsonNode;
import kit.corp.config.TaskConfiguration;
import kit.corp.freebie.MarketCheck;
import kit.corp.freebie.MarketCheckType;
import kit.corp.freebie.market.MarkerCheckYandex;
import kit.corp.freebie.market.MarketCheckOzon;
import kit.corp.freebie.market.MarketCheckWb;
import kit.corp.model.product.Product;
import kit.corp.model.product.dto.SaveNewProduct;
import kit.corp.model.task.TaskExecution;
import kit.corp.model.task.TaskStatus;
import kit.corp.repository.ProductRepository;
import kit.corp.repository.TaskExecutionRepository;
import lombok.RequiredArgsConstructor;
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
public class CheckService {
    private final ProductRepository productRepository;
    private final TaskExecutionRepository taskExecutionRepository;
    private final TaskConfiguration taskConfiguration;

    @Scheduled(fixedDelayString = "${tasks.start.time-delay}", timeUnit = TimeUnit.MINUTES)
    public void start() {
        String taskName = taskConfiguration.getStart().getTaskName();
        if (taskExecutionRepository.isRunningTask(taskName, TaskStatus.RUNNING)) {
            System.out.println(taskName + " isn't completed yet.");
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

        productRepository.save(product);
    }

    private TaskExecution createTaskExecution(String taskName) {
        List<String> taskParams = List.of(
                String.valueOf(taskConfiguration.getStart().getTimeDelay()),
                String.valueOf(taskConfiguration.isNeedAuth())
        );

        return TaskExecution.builder()
                .taskName(taskName)
                .taskStatus(TaskStatus.RUNNING)
                .startTime(new Timestamp(System.currentTimeMillis()))
                .startParams(String.join(";", taskParams))
                .build();
    }

    private TaskResult processProducts() {
        long countElems = productRepository.count();
        long countUpdateElems = 0;
        long countErrors = 0;

        if (countElems > 0) {
            List<Product> products = productRepository.findAll();
            for (Product product : products) {
                try {
                    MarketCheck check = getMarketCheck(product.getMarket(), product.getArticle());
                    Document document = check.fetch();
                    JsonNode jsonNode = check.extract(document);
                    Product checkProduct = check.getPrice(jsonNode);

                    Product updatedProduct = checkPrice(product, checkProduct);
                    edit(updatedProduct);
                    countUpdateElems++;
                } catch (Exception e) {
                    countErrors++;
                    System.out.printf("%s with error: %s%n", product.getArticle(), e.getMessage());
                }
            }
        } else {
            System.out.println("Nothing to check");
        }

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

        taskExecutionRepository.save(taskExecution);
    }

    private MarketCheck getMarketCheck(final MarketCheckType type, final String article) {
        return switch (type) {
            case YANDEX -> new MarkerCheckYandex(article);
            case OZON -> new MarketCheckOzon(article);
            case WB -> new MarketCheckWb(article);
        };
    }

    private Product checkPrice(final Product product, final Product checkProduct) {
        String currentTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        if (product.getCheckTime() == null) {
            product.setCheckTime(checkProduct.getCheckTime());
            product.setLastPrice(checkProduct.getLastPrice());
            product.setPriceWithDiscount(checkProduct.getPriceWithDiscount());
            product.setPrice(checkProduct.getPrice());

            System.out.println("Текущее время: " + currentTime);
            System.out.println(product);

            return product;
        }
        if (product.getPrice() > checkProduct.getPrice()) {
            product.setCheckTime(checkProduct.getCheckTime());
            product.setPrice(checkProduct.getPrice());
            product.setLastPrice(checkProduct.getLastPrice());
            product.setPriceWithDiscount(checkProduct.getPriceWithDiscount());

            System.out.println("Текущее время: " + currentTime);
            System.out.println(product + " цена поменялась.");

            return product;
        } else {
            product.setCheckTime(checkProduct.getCheckTime());
            System.out.println("Текущее время: " + currentTime);
            System.out.println(product + " цена не поменялась.");

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
    }

    private record TaskResult(long total, long updates, long errors) {}
}
