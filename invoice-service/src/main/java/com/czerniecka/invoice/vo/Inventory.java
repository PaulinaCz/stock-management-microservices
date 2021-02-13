package com.czerniecka.invoice.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Inventory {

    private String id;
    private String productId;
    private int quantity;
    private LocalDateTime lastModified;

}
