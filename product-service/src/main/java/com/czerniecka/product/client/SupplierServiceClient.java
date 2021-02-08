package com.czerniecka.product.client;

import com.czerniecka.product.vo.Supplier;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.UUID;

@Service
public class SupplierServiceClient {

    private final RestTemplate restTemplate;

    @Autowired
    public SupplierServiceClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @CircuitBreaker(name="supplier-service", fallbackMethod = "fallback")
    public Supplier getSupplier(UUID supplierId){
        return restTemplate.getForObject("http://supplier-service/suppliers/" + supplierId,
                Supplier.class);
    }

    public Supplier fallback(UUID supplierId, Throwable throwable){
        System.out.println("Service is currently busy. Please try again later.");
        return new Supplier();
    }

}
