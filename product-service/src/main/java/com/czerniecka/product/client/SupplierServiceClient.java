package com.czerniecka.product.client;

import com.czerniecka.product.dto.ProductDTO;
import com.czerniecka.product.vo.ProductSupplierResponse;
import com.czerniecka.product.vo.Supplier;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.circuitbreaker.ReactiveCircuitBreaker;
import org.springframework.cloud.client.circuitbreaker.ReactiveCircuitBreakerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;


@Service
public class SupplierServiceClient {

    private WebClient.Builder webClientBuilder;
    final ReactiveCircuitBreaker rcb;

    @Autowired
    public SupplierServiceClient(WebClient.Builder webClientBuilder, ReactiveCircuitBreakerFactory cbFactory) {
        this.webClientBuilder = webClientBuilder;
        this.rcb = cbFactory.create("supplier-service-cb");
    }

    public Mono<ProductSupplierResponse> getSupplier(String supplierId, ProductDTO product){

        Mono<ProductSupplierResponse> response = webClientBuilder.build()
                .get()
                .uri("http://supplier-service/suppliers/" + supplierId)
                .retrieve()
                .bodyToMono(Supplier.class)
                .flatMap(supplier -> Mono.just(new ProductSupplierResponse(product, supplier)));

        return rcb.run(response, throwable -> Mono.just(new ProductSupplierResponse(product, new Supplier())));
    }
}
