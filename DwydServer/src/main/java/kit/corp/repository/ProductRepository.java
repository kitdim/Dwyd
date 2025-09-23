package kit.corp.repository;

import kit.corp.model.product.Product;
import kit.corp.model.product.ProductProcessType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    @Query("SELECT p FROM Product p " +
            "WHERE p.productProcessType = :productProcessType")
    List<Product> findAllProductsByProcessType(@Param("productProcessType") ProductProcessType productProcessType);
}
