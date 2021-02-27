package com.czerniecka.order.client;

import com.czerniecka.order.dto.OrderDTO;
import com.czerniecka.order.vo.OrderProductResponse;
import com.czerniecka.order.vo.Product;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import javax.naming.ServiceUnavailableException;


@Service
public class ProductServiceClient {

    private WebClient.Builder webClientBuilder;

    @Autowired
    public ProductServiceClient(WebClient.Builder webClientBuilder) {
        this.webClientBuilder = webClientBuilder;
    }
    
    @CircuitBreaker(name = "product-service-cb", fallbackMethod = "productFallback")
    public Mono<OrderProductResponse> getProduct(String productId, OrderDTO order){

        return webClientBuilder.build()
                .get()
                .uri("http://product-service/products/" + productId)
                .retrieve()
                .bodyToMono(Product.class)
                .onErrorReturn(new Product())
                .flatMap(product -> Mono.just(new OrderProductResponse(order, product)));
    }
    public Mono<OrderProductResponse> productFallback(String productId, OrderDTO order, Throwable throwable){
        return Mono.error(new ServiceUnavailableException("Service is currently busy. Please try again later."));
    }
}
