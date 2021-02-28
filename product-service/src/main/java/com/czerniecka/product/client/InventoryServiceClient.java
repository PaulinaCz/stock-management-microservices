package com.czerniecka.product.client;

import com.czerniecka.product.vo.Inventory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.circuitbreaker.ReactiveCircuitBreaker;
import org.springframework.cloud.client.circuitbreaker.ReactiveCircuitBreakerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import javax.naming.ServiceUnavailableException;

@Service
public class InventoryServiceClient {

    private WebClient.Builder webClientBuilder;
    final ReactiveCircuitBreaker rcb;

    @Autowired
    public InventoryServiceClient(WebClient.Builder webClientBuilder, ReactiveCircuitBreakerFactory cbFactory) {
        this.webClientBuilder = webClientBuilder;
        this.rcb = cbFactory.create("inventory-service-cb");
    }

    public Mono<Inventory> postInventory(Inventory inventory){

        Mono<Inventory> i = webClientBuilder.build()
                .post()
                .uri("http://inventory-service/inventories")
                .body(Mono.just(inventory), Inventory.class)
                .retrieve()
                .bodyToMono(Inventory.class);

        return rcb.run(i, throwable -> Mono.error(
                               new ServiceUnavailableException("Error while creating product inventory. Product not saved.")));

    }
}
