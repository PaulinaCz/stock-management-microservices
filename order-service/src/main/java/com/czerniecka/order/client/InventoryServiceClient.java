package com.czerniecka.order.client;

import com.czerniecka.order.vo.Inventory;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;


@Service
public class InventoryServiceClient {

    private WebClient.Builder webClientBuilder;
    private final RestTemplate restTemplate;

    @Autowired
    public InventoryServiceClient(WebClient.Builder webClientBuilder, RestTemplate restTemplate) {
        this.webClientBuilder = webClientBuilder;
        this.restTemplate = restTemplate;
    }
    
    @CircuitBreaker(name = "inventory-service", fallbackMethod = "fallbackGet")
    public Inventory getInventory(String productId){

        return webClientBuilder.build()
                .get()
                .uri("http://inventory-service/inventories/product/" + productId)
                .retrieve()
                .bodyToMono(Inventory.class)
                .block();

    }
    
    public Inventory fallbackGet(String inventoryId, Throwable throwable){
        System.out.println("Service is currently busy. Please try again later.");
        return new Inventory();
    }

    @CircuitBreaker(name = "inventory-service", fallbackMethod = "fallbackPut")
    public Inventory putInventory(Inventory inventory){

        return webClientBuilder.build()
                .put()
                .uri("http://inventory-service/inventories/" + inventory.getId())
                .body(Mono.just(inventory), Inventory.class)
                .retrieve()
                .onStatus(HttpStatus.NOT_FOUND::equals,
                        clientResponse -> Mono.empty())
                .bodyToMono(Inventory.class)
                .block();

    }

    public Inventory fallbackPut(Inventory inventory, Throwable throwable){
        System.out.println("Error while proceeding order, please try again later.");
        return new Inventory();
    }
}
