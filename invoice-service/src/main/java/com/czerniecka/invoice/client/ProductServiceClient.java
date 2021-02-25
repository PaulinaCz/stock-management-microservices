package com.czerniecka.invoice.client;

import com.czerniecka.invoice.dto.InvoiceDTO;
import com.czerniecka.invoice.vo.InvoiceProductResponse;
import com.czerniecka.invoice.vo.Product;
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

//    @CircuitBreaker(name = "product-service", fallbackMethod = "fallback")
    public Mono<InvoiceProductResponse> getProduct(String productId, InvoiceDTO invoice) {

        return webClientBuilder.build()
                .get()
                .uri("http://product-service/products/" + productId)
                .retrieve()
                .bodyToMono(Product.class)
                .onErrorReturn(new Product())
                .flatMap(product -> Mono.just(new InvoiceProductResponse(invoice, product)));
    }

//    public Mono<Product> fallback(String productId, Throwable throwable) {
//        return Mono.error(new ServiceUnavailableException("Service is currently busy. Please try again later."));
//    }

}
