package com.czerniecka.invoice.vo;

import com.czerniecka.invoice.dto.InvoiceDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import reactor.core.publisher.Mono;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class InvoiceProductResponse {
    
    private InvoiceDTO invoice;
    private Mono<Product> product;
}
