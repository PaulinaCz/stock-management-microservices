package com.czerniecka.product.client;

import com.czerniecka.product.dto.ProductDTO;
import com.czerniecka.product.exceptions.ProductNotFound;
import com.czerniecka.product.vo.ProductSupplierResponse;
import com.czerniecka.product.vo.Supplier;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import javax.naming.ServiceUnavailableException;

@Service
public class SupplierServiceClient {


    private WebClient.Builder webClientBuilder;

    @Autowired
    public SupplierServiceClient(WebClient.Builder webClientBuilder) {
        this.webClientBuilder = webClientBuilder;
    }

//    @CircuitBreaker(name="supplier-service", fallbackMethod = "fallback")
//    public Mono<ProductSupplierResponse> getProductWithSupplier(String productId){
//
//        return webClientBuilder.build()
//                .get()
//                .uri("http://product-service/products/" + productId)
//                .retrieve()
//                .bodyToMono(ProductDTO.class)
//                .switchIfEmpty(Mono.error(new ProductNotFound(productId)))
//                .flatMap(
//                        p -> {
//                              Mono<Supplier> s = webClientBuilder.build()
//                                      .get()
//                                      .uri("http://supplier-service/suppliers/")
//                                    .retrieve()
//                                    .bodyToMono(Supplier.class)
//                                    .onErrorResume(e -> Mono.error(
//                                            new ServiceUnavailableException(
//                                                    "Service is currently busy. Please try again later")));
//                              return Mono.just(new ProductSupplierResponse(p, s));
//                        }
//                );
//
//
//    }


    @CircuitBreaker(name="supplier-service", fallbackMethod = "fallback")
    public Supplier getSupplier(String supplierId){

        return webClientBuilder.build()
                .get()
                .uri("http://supplier-service/suppliers/" + supplierId)
                .retrieve()
                .bodyToMono(Supplier.class)
                .onErrorResume(e -> Mono.error(
                        new ServiceUnavailableException(
                                "Service is currently busy. Please try again later")
                ))
                .block();

    }

    public Supplier fallback(String supplierId, Throwable throwable){
        return new Supplier();
    }

}
