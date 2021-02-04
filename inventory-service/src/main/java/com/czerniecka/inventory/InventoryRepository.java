package com.czerniecka.inventory;

import com.czerniecka.inventory.entity.Inventory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface InventoryRepository extends JpaRepository<Inventory, UUID> {

    @Query(value= "SELECT i FROM Inventory i WHERE i.productId=?1")
    Optional<Inventory> findByProductId(UUID productId);
}
