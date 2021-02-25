package com.czerniecka.product.dto;

import lombok.*;
import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductDTO {

    private String id;
    private String name;
    private BigDecimal sellingPrice;
    private BigDecimal buyingPrice;
    private String category;
    private String supplierId;
}
