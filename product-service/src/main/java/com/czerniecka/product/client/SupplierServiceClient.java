package com.czerniecka.product.client;

import com.czerniecka.product.dto.ProductDTO;
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
    
    @CircuitBreaker(name="supplier-service-cb", fallbackMethod = "supplierFallback")
    public Mono<ProductSupplierResponse> getSupplier(String supplierId, ProductDTO product){

        return webClientBuilder.build()
                .get()
                .uri("http://supplier-service/suppliers/" + supplierId)
                .retrieve()
                .bodyToMono(Supplier.class)
                .onErrorReturn(new Supplier())
                .flatMap(supplier -> Mono.just(new ProductSupplierResponse(product, supplier)));
    }

    public Mono<ProductSupplierResponse> supplierFallback(String supplierId, Throwable throwable){
        return Mono.error(new ServiceUnavailableException("Service is currently busy. Please try again later."));
    }

}
