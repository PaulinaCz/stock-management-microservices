package com.czerniecka.invoice.client;

import com.czerniecka.invoice.vo.Inventory;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
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

    public Inventory fallbackGet(UUID productId, Throwable throwable){
        System.out.println("Service is currently busy. Please try again later.");
        
        return new Inventory();
    }

    @CircuitBreaker(name = "inventory-service", fallbackMethod = "fallbackPut")
    public HttpStatus putInventory(Inventory inventory){

        HttpEntity request = new HttpEntity(inventory);
        return restTemplate.exchange("http://inventory-service/inventory/" + inventory.getId(),
                HttpMethod.PUT, request, Void.class ).getStatusCode();
    }

    public HttpStatus fallbackPut(Inventory inventory, Throwable throwable){
        System.out.println("Error while processing invoice, please try again later. ");
        return HttpStatus.SERVICE_UNAVAILABLE;
    }

}
