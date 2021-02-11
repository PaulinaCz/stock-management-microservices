package com.czerniecka.product.repository;

import com.czerniecka.product.entity.Product;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

import java.util.UUID;

@Repository
public interface ProductRepository extends ReactiveCrudRepository<Product, String> {

    @Query(value = "{ 'category' : {$regex : ?0, $options: 'i'}}")
    Flux<Product> findProductByCategoryContaining(String category);
    
    Flux<Product> findAllBySupplierId(String supplierId);


}
