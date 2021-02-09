package com.czerniecka.product.dto;

import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;
import java.math.BigDecimal;
import java.util.UUID;

@Data
public class ProductDTO {

    @NotBlank(message = "Please provide product name")
    private String name;
    @Positive
    private BigDecimal buyingPrice;
    @Positive
    private BigDecimal sellingPrice;
    @NotBlank(message = "Please provide category")
    @Size(max = 20, message = "Category cannot be longer than 20 characters")
    private String category;
    @NotNull(message = "Please add supplier")
    private UUID supplierId;
}
