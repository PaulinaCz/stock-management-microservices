package com.czerniecka.product.service;

import com.czerniecka.product.vo.Inventory;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class InventoryServiceClient {

    private final RestTemplate restTemplate;

    @Autowired
    public InventoryServiceClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @CircuitBreaker(name="inventory-service", fallbackMethod = "fallback")
    public Inventory postInventory(Inventory inventory){
        return restTemplate.postForObject("http://inventory-service/inventory", inventory, Inventory.class);
    }

    public Inventory fallback(Inventory inventory, Throwable throwable){
        return new Inventory();
    }
}
