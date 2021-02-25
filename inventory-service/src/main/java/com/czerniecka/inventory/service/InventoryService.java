package com.czerniecka.inventory.service;

import com.czerniecka.inventory.client.ProductServiceClient;
import com.czerniecka.inventory.repository.InventoryRepository;
import com.czerniecka.inventory.dto.InventoryDTO;
import com.czerniecka.inventory.dto.InventoryMapper;
import com.czerniecka.inventory.entity.Inventory;
import com.czerniecka.inventory.vo.InventoryProductResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

@Service
public class InventoryService {

    private final InventoryRepository inventoryRepository;
    private final InventoryMapper inventoryMapper;
    private final ProductServiceClient productServiceClient;

    @Autowired
    public InventoryService(InventoryRepository inventoryRepository, InventoryMapper inventoryMapper, ProductServiceClient productServiceClient) {
        this.inventoryRepository = inventoryRepository;
        this.inventoryMapper = inventoryMapper;
        this.productServiceClient = productServiceClient;
    }

    public Flux<InventoryDTO> findAll() {

        Flux<Inventory> all = inventoryRepository.findAll();
        return all.map(inventoryMapper::toInventoryDTO);
    }

    /**
     *  When product-service is not available method returns 
     *  Flux of InventoryProductResponse
     *  where each IPR has
     *  Inventory object and empty Product object
     */
    public Flux<InventoryProductResponse> findAllWithProducts() {
        Flux<Inventory> inventories = inventoryRepository.findAll();

        return inventories.flatMap(
                i -> productServiceClient.getProduct(i.getProductId(), inventoryMapper.toInventoryDTO(i))
        );
    }

    /**
     *  If product-service is not available method returns:
     *  Mono of InventoryProductResponse
     *  with Inventory object and empty Product object
     *
     *  If inventory is not found returns empty Mono
     *  which will return "Inventory not found error" in controller
     */
    public Mono<InventoryProductResponse> findInventoryById(String inventoryId) {
        var inventory = inventoryRepository.findById(inventoryId);
        return inventory.switchIfEmpty(Mono.empty())
                .flatMap(i -> productServiceClient.getProduct(inventoryId, inventoryMapper.toInventoryDTO(i)));
    }

    public Mono<InventoryDTO> findInventoryByProductId(String productId) {
        Mono<Inventory> inventory = inventoryRepository.findByProductId(productId);
        return inventory.map(inventoryMapper::toInventoryDTO);
    }

    public Mono<InventoryDTO> save(InventoryDTO inventoryDTO) {
        Inventory inventory = inventoryMapper.toInventory(inventoryDTO);
        Mono<Inventory> saved = inventoryRepository.save(inventory);
        return saved.map(inventoryMapper::toInventoryDTO);
    }

    public Mono<InventoryDTO> updateInventory(String inventoryId, InventoryDTO inventoryDTO) {
        
        return inventoryRepository.findById(inventoryId)
                .switchIfEmpty(Mono.empty())
                .flatMap(inventory -> {
                            inventory.setQuantity(inventoryDTO.getQuantity());
                            inventory.setLastModified(LocalDateTime.now());
                            Mono<Inventory> updated = inventoryRepository.save(inventory);
                            return updated.map(inventoryMapper::toInventoryDTO);
                        });
    }


}
