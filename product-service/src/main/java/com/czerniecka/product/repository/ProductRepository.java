package com.czerniecka.product.repository;

import com.czerniecka.product.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ProductRepository extends JpaRepository<Product, UUID> {

    @Query(value = "SELECT p FROM Product p WHERE p.category LIKE %?1%")
    List<Product> findProductByCategoryContaining(String category);
    
    List<Product> findAllBySupplierId(UUID supplierId);


}
