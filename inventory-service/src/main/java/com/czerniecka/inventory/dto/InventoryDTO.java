package com.czerniecka.inventory.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InventoryDTO {

    private String id;

    @NotNull(message = "Product Id must not be null")
    private String productId;

    @NotNull
    private int quantity;
    private LocalDateTime lastModified = LocalDateTime.now();
}
