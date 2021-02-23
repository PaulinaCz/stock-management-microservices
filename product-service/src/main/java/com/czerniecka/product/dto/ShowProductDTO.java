package com.czerniecka.product.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ShowProductDTO {

    private String id;
    private String name;
    private BigDecimal sellingPrice;
    private String category;
    private String supplierId;
}
