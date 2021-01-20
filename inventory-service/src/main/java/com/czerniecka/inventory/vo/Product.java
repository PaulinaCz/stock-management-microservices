package com.czerniecka.inventory.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Product {


    private UUID id;
    private String name;
    private String description;
    private BigDecimal sellingPrice;
    private BigDecimal buyingPrice;
    private String category;

}
