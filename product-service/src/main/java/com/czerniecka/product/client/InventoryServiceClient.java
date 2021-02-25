package com.czerniecka.product.client;

import com.czerniecka.product.vo.Inventory;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import javax.naming.ServiceUnavailableException;

@Service
public class InventoryServiceClient {

    private WebClient.Builder webClientBuilder;


    @Autowired
    public InventoryServiceClient(WebClient.Builder webClientBuilder) {
        this.webClientBuilder = webClientBuilder;
    }
    
//    @CircuitBreaker(name="inventory-service", fallbackMethod = "fallback")
    public Mono<Inventory> postInventory(Inventory inventory){

        return webClientBuilder.build()
                .post()
                .uri("http://inventory-service/inventories")
                .body(Mono.just(inventory), Inventory.class)
                .retrieve()
                .bodyToMono(Inventory.class)
                .onErrorResume(e -> Mono.error(
                        new ServiceUnavailableException("Error while creating product inventory. Product not saved.")
                ));

    }
//    public Mono<Inventory> fallback(Inventory inventory, Throwable throwable){
//        return Mono.error(new ServiceUnavailableException("Error while creating product inventory. Product not saved."));
//    }
}
