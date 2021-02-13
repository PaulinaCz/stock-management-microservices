package com.czerniecka.order.entity;


import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.UUID;

@Document(collection = "orders")
@Data
@NoArgsConstructor
public class Order {

    @Id
    private String id = UUID.randomUUID().toString();
    private String paymentType;
    private String orderStatus;

    @CreatedDate
    private LocalDateTime orderPlaced = LocalDateTime.now();

    private String productId;
    private int amount;
    private String customerId;

    public void setOrderStatus(String orderStatus) {
        this.orderStatus = "Order placed";
    }
}
