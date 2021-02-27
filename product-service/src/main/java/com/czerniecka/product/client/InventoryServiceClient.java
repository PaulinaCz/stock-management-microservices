package com.czerniecka.product.client;

import com.czerniecka.product.vo.Inventory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.circuitbreaker.ReactiveCircuitBreakerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import javax.naming.ServiceUnavailableException;

@Service
public class InventoryServiceClient {

    private WebClient.Builder webClientBuilder;
    private ReactiveCircuitBreakerFactory cbFactory;

    @Autowired
    public InventoryServiceClient(WebClient.Builder webClientBuilder, ReactiveCircuitBreakerFactory cbFactory) {
        this.webClientBuilder = webClientBuilder;
        this.cbFactory = cbFactory;
    }

    public Mono<Inventory> postInventory(Inventory inventory){

        return webClientBuilder.build()
                .post()
                .uri("http://inventory-service/inventories")
                .body(Mono.just(inventory), Inventory.class)
                .retrieve()
                .bodyToMono(Inventory.class)
                .transform(
                        it -> cbFactory.create("inventory-service-cb")
                        .run(it, throwable -> Mono.error(
                                new ServiceUnavailableException("Error while creating product inventory. Product not saved.")
                        ))
                );

    }
}
