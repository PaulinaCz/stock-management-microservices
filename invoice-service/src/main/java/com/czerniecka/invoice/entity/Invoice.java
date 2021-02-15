package com.czerniecka.invoice.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.UUID;

@Document(collection = "invoices")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Invoice {

    @Id
    private String id = UUID.randomUUID().toString();
    
    private String productId;
    private int amount;
    @CreatedDate
    private LocalDateTime datePlaced = LocalDateTime.now();

    private String supplierId;
}
