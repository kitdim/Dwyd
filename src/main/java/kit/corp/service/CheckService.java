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
import kit.corp.model.task.dto.TaskFinish;
import kit.corp.model.task.dto.TaskStart;
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

@Service
@RequiredArgsConstructor
public class CheckService {
    private final ProductRepository productRepository;
    private final TaskExecutionRepository taskExecutionRepository;
    private final TaskConfiguration taskConfiguration;

    @Scheduled()
    public void start() {
        String taskName = taskConfiguration.getStartTask().getTaskName();
        List<String> taskParams = List.of(
                taskConfiguration.getStartTask().getTimeDelay().toString(),
                String.valueOf(taskConfiguration.isNeedAuth())
        );
        Timestamp startTime = new Timestamp(System.currentTimeMillis());
        TaskStart startTask = new TaskStart(taskName, TaskStatus.RUNNING, taskParams, startTime);

        TaskExecution taskExecution = new TaskExecution();
        taskExecution.setTaskStatus(startTask.taskStatus());
        taskExecution.setStartTime(startTask.startTime());
        taskExecution.setStartParams(String.join(";", startTask.taskParams()));
        taskExecution.setTaskName(startTask.taskName());

        taskExecutionRepository.save(taskExecution);

        long countElems = productRepository.count();
        long countUpdateElems = 0;
        long countErrors = 0;

        if (countElems > 0) {
            List<Product> products = productRepository.findAll();
            for (Product product : products) {
                MarketCheck check = getMarketCheck(product.getMarket(), product.getArticle());
                Document document;
                JsonNode jsonNode;
                Product checkProduct;

                try {
                    document = check.fetch();
                    jsonNode = check.extract(document);
                    checkProduct = check.getPrice(jsonNode);
                } catch (Exception e) {
                    countErrors++;
                    System.out.println(product.getArticle() + " with error: " + e.getMessage());
                    continue;
                }

                Product checkPriceProduct = checkPrice(product, checkProduct);
                edit(checkPriceProduct);
                countUpdateElems++;
            }
        } else {
            System.out.println("Нечего проверять");
        }

        String description = """
                Count elems: {%d} \n
                Count error: {%d} \n
                Count update: {%d} \n
                """.formatted(countElems, countErrors, countUpdateElems);

        Timestamp finishTime = new Timestamp(System.currentTimeMillis());
        TaskFinish taskFinish;

        if (countErrors > 0) {
            taskFinish = new TaskFinish(description, TaskStatus.ERROR, finishTime);
        } else {
            taskFinish = new TaskFinish(description, TaskStatus.FINISH, finishTime);
        }

        taskExecution.setFinishTime(taskFinish.finishTime());
        taskExecution.setDescription(taskFinish.description());
        taskExecution.setTaskStatus(taskFinish.taskStatus());

        taskExecutionRepository.save(taskExecution);
    }

    public void saveNew(final SaveNewProduct saveNewProduct) {
        Product product = new Product();
        product.setMarket(saveNewProduct.marketCheckType());
        product.setArticle(saveNewProduct.article());

        productRepository.save(product);
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
}
