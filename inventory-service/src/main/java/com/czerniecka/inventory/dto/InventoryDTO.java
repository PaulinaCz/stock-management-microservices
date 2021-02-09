package com.czerniecka.inventory.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class InventoryDTO {

    private UUID id;

    @NotNull(message = "Product Id must not be null")
    private UUID productId;

    @NotNull
    private int quantity;
    private LocalDateTime lastModified;
}
