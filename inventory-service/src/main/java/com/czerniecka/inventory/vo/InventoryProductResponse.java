package com.czerniecka.inventory.vo;

import com.czerniecka.inventory.dto.InventoryDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import reactor.core.publisher.Mono;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InventoryProductResponse {

    private InventoryDTO inventory;
    private Mono<Product> product;
}
