package com.czerniecka.inventory.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class InventoryDTO {

    private UUID id;
    private UUID productId;
    private int quantity;
    private LocalDateTime lastModified;
}
