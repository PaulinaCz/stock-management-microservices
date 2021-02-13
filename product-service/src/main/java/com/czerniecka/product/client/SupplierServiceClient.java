package com.czerniecka.product.client;

import com.czerniecka.product.vo.Supplier;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
public class SupplierServiceClient {


    private WebClient.Builder webClientBuilder;

    @Autowired
    public SupplierServiceClient(WebClient.Builder webClientBuilder) {
        this.webClientBuilder = webClientBuilder;
    }

    @CircuitBreaker(name="supplier-service", fallbackMethod = "fallback")
    public Supplier getSupplier(String supplierId){

        return webClientBuilder.build()
                .get()
                .uri("http://supplier-service/suppliers/" + supplierId)
                .retrieve()
                .bodyToMono(Supplier.class)
                .block();

    }

    public Supplier fallback(String supplierId, Throwable throwable){
        System.out.println("Service is currently busy. Please try again later.");
        return new Supplier();
    }

}
