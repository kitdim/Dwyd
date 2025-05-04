package kit.corp.repository;

import kit.corp.model.Product;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ProductRepository {
    Optional<Product> getById(UUID id);
    Optional<Boolean> save(Product product);
    Optional<Boolean> deleteById(UUID id);
    void edit(Product product);
    boolean isEmpty();
    List<Product> getAll();
}
