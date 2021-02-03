package com.czerniecka.invoice.service;

import com.czerniecka.invoice.vo.Inventory;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.UUID;

@Service
public class InventoryServiceClient {

    private final RestTemplate restTemplate;

    @Autowired
    public InventoryServiceClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @CircuitBreaker(name = "inventory-service", fallbackMethod = "fallbackGet")
    public Inventory getInventory(UUID productId){
        return restTemplate.getForObject("http://inventory-service/inventory/product/" + productId
                ,Inventory.class);
    }

    public Inventory fallbackGet(UUID inventoryId, Throwable throwable){
        System.out.println("Service is currently busy. Please try again later.");
        return new Inventory();
    }

    // Put/Patch method not working !

    @CircuitBreaker(name = "inventory-service", fallbackMethod = "fallbackPut")
    public Inventory putInventory(Inventory inventory){
        return restTemplate.patchForObject("http://inventory-service/inventory/inventory/" + inventory.getId(), inventory, Inventory.class);
    }

    public Inventory fallbackPut(Inventory inventory, Throwable throwable){
        System.out.println("Service not available! Invoice is not proceed!");
        return new Inventory();
    }

}
