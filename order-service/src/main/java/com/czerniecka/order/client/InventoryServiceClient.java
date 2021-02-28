package com.czerniecka.order.client;

import com.czerniecka.order.dto.OrderDTO;
import com.czerniecka.order.exceptions.ItemNotAvailable;
import com.czerniecka.order.vo.Inventory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.circuitbreaker.ReactiveCircuitBreaker;
import org.springframework.cloud.client.circuitbreaker.ReactiveCircuitBreakerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import javax.naming.ServiceUnavailableException;

@Service
public class InventoryServiceClient {

    private WebClient.Builder webClientBuilder;
    final ReactiveCircuitBreaker rcb;

    @Autowired
    public InventoryServiceClient(WebClient.Builder webClientBuilder, ReactiveCircuitBreakerFactory cbFactory) {
        this.webClientBuilder = webClientBuilder;
        this.rcb = cbFactory.create("inventory-service-cb");
    }

    public Mono<Inventory> updateInventory(OrderDTO orderDTO) {

        return webClientBuilder.build()
                .get()
                .uri("http://inventory-service/inventories/product/" + orderDTO.getProductId())
                .retrieve()
                .bodyToMono(Inventory.class)
                .onErrorResume(e -> Mono.error(
                        new ServiceUnavailableException("Service is currently busy. Please try again later")
                ))
                .flatMap(inventory -> {
                    if(inventory.getQuantity() > orderDTO.getAmount()) {
                        inventory.setQuantity(inventory.getQuantity() - orderDTO.getAmount());
                        return webClientBuilder.build()
                                .put()
                                .uri("http://inventory-service/inventories/" + inventory.getId())
                                .body(Mono.just(inventory), Inventory.class)
                                .retrieve()
                                .bodyToMono(Inventory.class)
                                .onErrorResume(e -> Mono.error(
                                        new ServiceUnavailableException("Error while proceeding order, please try again later.")
                                ));
                    }else{
                        return Mono.error(new ItemNotAvailable("Not enough items in stock."));
                    }
                });
    }
}
