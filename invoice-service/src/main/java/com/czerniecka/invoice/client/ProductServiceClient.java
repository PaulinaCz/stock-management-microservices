package com.czerniecka.invoice.client;

import com.czerniecka.invoice.dto.InvoiceDTO;
import com.czerniecka.invoice.vo.InvoiceProductResponse;
import com.czerniecka.invoice.vo.Product;
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

    public Mono<InvoiceProductResponse> getProduct(String productId, InvoiceDTO invoice) {

        Mono<InvoiceProductResponse> response = webClientBuilder.build()
                .get()
                .uri("http://product-service/products/" + productId)
                .retrieve()
                .bodyToMono(Product.class)
                .flatMap(product -> Mono.just(new InvoiceProductResponse(invoice, product)));

        return rcb.run(response, throwable -> Mono.just(new InvoiceProductResponse(invoice, new Product())));
    }
}
