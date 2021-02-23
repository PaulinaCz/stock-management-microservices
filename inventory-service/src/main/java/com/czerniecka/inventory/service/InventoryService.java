package com.czerniecka.inventory.service;

import com.czerniecka.inventory.client.ProductServiceClient;
import com.czerniecka.inventory.repository.InventoryRepository;
import com.czerniecka.inventory.dto.InventoryDTO;
import com.czerniecka.inventory.dto.InventoryMapper;
import com.czerniecka.inventory.entity.Inventory;
import com.czerniecka.inventory.vo.Product;
import com.czerniecka.inventory.vo.InventoryProductResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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
     *  List of Inventories where each has empty Product object
     */
    public Flux<List<InventoryProductResponse>> findAllWithProducts() {
        Flux<Inventory> inventories = inventoryRepository.findAll();
        List<InventoryProductResponse> result = new ArrayList<>();

        inventories.map(
                i -> {
                    Mono<Product> product = productServiceClient.getProduct(i.getProductId());
                    InventoryProductResponse vo = new InventoryProductResponse();
                    vo.setInventory(inventoryMapper.toInventoryDTO(i));
                    vo.setProduct(product);
                    result.add(vo);
                    return result;
                });
        return Flux.just(result);
    }

    /**
     *  When product-service is not available method returns:
     *  Inventory with empty Product object 
     *  
     *  If inventory is not found returns Mono.empty
     */
    public Mono<InventoryProductResponse> findInventoryById(String inventoryId) {
        Mono<Inventory> byId = inventoryRepository.findById(inventoryId);
        return byId.switchIfEmpty(Mono.empty())
                .flatMap(inventory -> {
                    InventoryProductResponse response = new InventoryProductResponse();
                    Mono<Product> product = productServiceClient.getProduct(inventory.getProductId());
                    response.setInventory(inventoryMapper.toInventoryDTO(inventory));
                    response.setProduct(product);
                    return Mono.just(response);
                });
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
                .flatMap(
                        inventory -> {
                            inventory.setQuantity(inventoryDTO.getQuantity());
                            inventory.setLastModified(LocalDateTime.now());
                            Mono<Inventory> updated = inventoryRepository.save(inventory);
                            return updated.map(inventoryMapper::toInventoryDTO);
                        });
    }


}
