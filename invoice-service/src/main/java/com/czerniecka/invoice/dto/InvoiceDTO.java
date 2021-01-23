package com.czerniecka.invoice.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class InvoiceDTO {

    private UUID id;
    private UUID productId;
    private int amount;
    private LocalDateTime datePlaced;
    private UUID supplierId;

}
