package com.czerniecka.product.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;
import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateProductDTO {

    @NotBlank(message = "Please provide product name")
    private String name;
    @NotNull
    @Positive
    private BigDecimal buyingPrice;
    @NotNull
    @Positive
    private BigDecimal sellingPrice;
    @NotBlank(message = "Please provide category")
    @Size(max = 20, message = "Category cannot be longer than 20 characters")
    private String category;
    @NotNull(message = "Please add supplier")
    private String supplierId;
}
