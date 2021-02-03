package com.czerniecka.product.dto;

import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

@Data
public class ProductDTO {
    
    private String name;
    private BigDecimal buyingPrice;
    private BigDecimal sellingPrice;
    private String category;
    private UUID supplierId;
}
