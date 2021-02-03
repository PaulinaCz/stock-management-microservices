package com.czerniecka.product.service;

import com.czerniecka.product.vo.Inventory;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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
    public HttpStatus postInventory(Inventory inventory){
        
        return restTemplate.postForEntity("http://inventory-service/inventory", inventory, Inventory.class).getStatusCode();

    }
    public HttpStatus fallback(Inventory inventory, Throwable throwable){
        return HttpStatus.SERVICE_UNAVAILABLE;
    }
}
