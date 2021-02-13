package com.czerniecka.order.client;

import com.czerniecka.order.vo.Product;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.UUID;

@Service
public class ProductServiceClient {

    private final RestTemplate restTemplate;

    @Autowired
    public ProductServiceClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @CircuitBreaker(name = "product-service", fallbackMethod = "fallback")
    public Product getProduct(String productId){
        return restTemplate.getForObject("http://product-service/products/" + productId,
                Product.class);
    }

    public Product fallback(String productId, Throwable throwable){
        System.out.println("Service is currently busy. Please try again later.");
        return new Product();
    }
}
