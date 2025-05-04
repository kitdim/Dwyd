package kit.corp.repository.impl;

import kit.corp.model.Product;
import kit.corp.repository.ProductRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class ProductRepositoryInMemory implements ProductRepository {
    private static final List<Product> repository = new ArrayList<>();

    @Override
    public Optional<Product> getById(UUID id) {
        if (id == null) {
            String error = "id is null.";
            throw new RuntimeException(error);
        }

        Optional<Product> maybeProduct = repository.stream()
                .filter(product -> product.getId() == id)
                .findFirst();

        if (maybeProduct.isEmpty()) {
            String error = "id:%d, not found.".formatted(id.node());
            throw new RuntimeException(error);
        }

        return maybeProduct;
    }

    @Override
    public Optional<Boolean> save(Product product) {
        if (product == null) {
            String error = "Data is null.";
            throw new RuntimeException(error);
        }
        Product saveProduct = new Product();

        saveProduct.setMarket(product.getMarket());
        saveProduct.setArticle(product.getArticle());
        saveProduct.setId(UUID.randomUUID());

        return Optional.of(repository.add(saveProduct));
    }

    @Override
    public Optional<Boolean> deleteById(UUID id) {
        Optional<Product> maybeProduct = getById(id);

        if (maybeProduct.isEmpty()) {
            String error = "id:%d, not found.".formatted(id.node());
            throw new RuntimeException(error);
        }

        return Optional.of(repository.remove(maybeProduct.get()));
    }


    @Override
    public void edit(Product product) {
        Product someProduct = getById(product.getId())
                .orElseThrow(() -> new RuntimeException("Product not found"));

        someProduct.setPrice(product.getPrice());
        someProduct.setPriceWithDiscount(product.getPriceWithDiscount());
        someProduct.setLastPrice(product.getLastPrice());
        someProduct.setCheckTime(product.getCheckTime());

        int index = repository.indexOf(someProduct);
        repository.add(index, someProduct);
    }

    @Override
    public boolean isEmpty() {
        return repository.isEmpty();
    }

    @Override
    public List<Product> getAll() {
        return new ArrayList<>(repository);
    }
}
