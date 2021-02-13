package com.czerniecka.inventory.repository;

import com.czerniecka.inventory.entity.Inventory;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

import java.util.UUID;


@Repository
public interface InventoryRepository extends ReactiveCrudRepository<Inventory, String> {

    Mono<Inventory> findByProductId(String productId);
}
