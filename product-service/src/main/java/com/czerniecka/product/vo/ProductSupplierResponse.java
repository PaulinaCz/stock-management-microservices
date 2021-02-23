package com.czerniecka.product.vo;

import com.czerniecka.product.dto.ProductDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import reactor.core.publisher.Mono;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductSupplierResponse {

    private ProductDTO product;
    private Mono<Supplier> supplier;
}
