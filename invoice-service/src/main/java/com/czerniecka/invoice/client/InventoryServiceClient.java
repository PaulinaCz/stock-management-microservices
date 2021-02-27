package com.czerniecka.invoice.client;

import com.czerniecka.invoice.dto.InvoiceDTO;
import com.czerniecka.invoice.vo.Inventory;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import javax.naming.ServiceUnavailableException;

@Service
public class InventoryServiceClient {

    private WebClient.Builder webClientBuilder;

    @Autowired
    public InventoryServiceClient(WebClient.Builder webClientBuilder) {
        this.webClientBuilder = webClientBuilder;
    }

    @CircuitBreaker(name = "inventory-service-cb", fallbackMethod = "fallbackUpdate")
    public Mono<Inventory> updateInventory(InvoiceDTO invoiceDTO) {

        return webClientBuilder.build()
                .get()
                .uri("http://inventory-service/inventories/product/" + invoiceDTO.getProductId())
                .retrieve()
                .bodyToMono(Inventory.class)
                .onErrorResume(e -> Mono.error(
                        new ServiceUnavailableException("Service is currently busy. Please try again later")
                ))
                .flatMap(inventory -> {
                    inventory.setQuantity(inventory.getQuantity() + invoiceDTO.getAmount());
                    return webClientBuilder.build()
                            .put()
                            .uri("http://inventory-service/inventories/" + inventory.getId())
                            .body(Mono.just(inventory), Inventory.class)
                            .retrieve()
                            .bodyToMono(Inventory.class)
                            .onErrorResume(e -> Mono.error(
                                    new ServiceUnavailableException("Error while processing invoice, please try again later.")
                            ));
                });
    }

    public Mono<Inventory> fallbackUpdate(InvoiceDTO invoiceDTO, Throwable throwable) {
        return Mono.error(new ServiceUnavailableException("Error while processing invoice, please try again later."));
    }

}
