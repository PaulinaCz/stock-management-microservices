package com.czerniecka.inventory.client;

import com.czerniecka.inventory.dto.InventoryDTO;
import com.czerniecka.inventory.vo.InventoryProductResponse;
import com.czerniecka.inventory.vo.Product;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;


@Service
public class ProductServiceClient {

    private WebClient.Builder webClientBuilder;

    @Autowired
    public ProductServiceClient(WebClient.Builder webClientBuilder) {
        this.webClientBuilder = webClientBuilder;
    }

//    @CircuitBreaker(name= "product-service", fallbackMethod = "fallback")
    public Mono<InventoryProductResponse> getProduct(String  productId, InventoryDTO inventory){
        
        return webClientBuilder.build()
                .get()
                .uri("http://product-service/products/" + productId)
                .retrieve()
                .bodyToMono(Product.class)
                .onErrorReturn(new Product())
                .flatMap(product -> Mono.just(new InventoryProductResponse(inventory, product)));
    }
//
//    public Mono<Product> fallback(String  productId, Throwable throwable){
//        return Mono.error(new ServiceUnavailableException("Service is currently busy. Please try again later."));
//    }
}
