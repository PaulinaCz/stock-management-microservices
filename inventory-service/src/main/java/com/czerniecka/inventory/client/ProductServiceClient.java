package com.czerniecka.inventory.client;

import com.czerniecka.inventory.dto.InventoryDTO;
import com.czerniecka.inventory.vo.InventoryProductResponse;
import com.czerniecka.inventory.vo.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.circuitbreaker.ReactiveCircuitBreaker;
import org.springframework.cloud.client.circuitbreaker.ReactiveCircuitBreakerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
public class ProductServiceClient {

    private WebClient.Builder webClientBuilder;
    final ReactiveCircuitBreaker rcb;

    @Autowired
    public ProductServiceClient(WebClient.Builder webClientBuilder, ReactiveCircuitBreakerFactory cbFactory) {
        this.webClientBuilder = webClientBuilder;
        this.rcb = cbFactory.create("product-service-cb");
    }

    public Mono<InventoryProductResponse> getProduct(String  productId, InventoryDTO inventory){

        Mono<InventoryProductResponse> response = webClientBuilder.build()
                .get()
                .uri("http://product-service/products/" + productId)
                .retrieve()
                .bodyToMono(Product.class)
                .flatMap(product -> Mono.just(new InventoryProductResponse(inventory, product)));

        return rcb.run(response, throwable -> Mono.just(new InventoryProductResponse(inventory, new Product())));
    }
}
