package com.czerniecka.inventory.client;

import com.czerniecka.inventory.vo.Product;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
public class ProductServiceClient {

    private WebClient.Builder webClientBuilder;

    @Autowired
    public ProductServiceClient(WebClient.Builder webClientBuilder) {
        this.webClientBuilder = webClientBuilder;
    }

    @CircuitBreaker(name= "product-service", fallbackMethod = "fallback")
    public Product getProduct(String  productId){
        
        return webClientBuilder.build()
                .get()
                .uri("http://product-service/products/" + productId)
                .retrieve()
                .bodyToMono(Product.class)
                .block();
    }

    public Product fallback(String  productId, Throwable throwable){
        System.out.println("Service is currently busy. Please try again later.");
        return new Product();
    }
}
