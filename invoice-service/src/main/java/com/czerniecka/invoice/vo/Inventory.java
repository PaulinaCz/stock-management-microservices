package com.czerniecka.invoice.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Inventory {

    private UUID id;
    private UUID productId;
    private int quantity;
    private LocalDateTime lastModified;

}
