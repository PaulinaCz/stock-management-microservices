package com.czerniecka.product.client;

import com.czerniecka.product.vo.Inventory;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
public class InventoryServiceClient {

    private WebClient.Builder webClientBuilder;


    @Autowired
    public InventoryServiceClient(WebClient.Builder webClientBuilder) {
        this.webClientBuilder = webClientBuilder;
    }
    
    @CircuitBreaker(name="inventory-service", fallbackMethod = "fallback")
    public Inventory postInventory(Inventory inventory){

        return webClientBuilder.build()
                .post()
                .uri("http://inventory-service/inventories")
                .header(MediaType.APPLICATION_JSON_VALUE)
                .body(Mono.just(inventory), Inventory.class)
                .retrieve()
                .onStatus(HttpStatus.BAD_REQUEST::equals,
                        clientResponse -> Mono.empty())
                .bodyToMono(Inventory.class)
                .block();

    }
    public Inventory fallback(Inventory inventory, Throwable throwable){
        System.out.println("Error while creating product inventory. Product not saved.");
        return new Inventory();
    }
}
