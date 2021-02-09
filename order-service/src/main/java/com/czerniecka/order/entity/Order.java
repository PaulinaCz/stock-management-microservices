package com.czerniecka.order.entity;


import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Data
@NoArgsConstructor
public class Order {

    @Id
    @GeneratedValue
    @Type(type="org.hibernate.type.UUIDCharType")
    private UUID id;
    private String paymentType;
    
    private String orderStatus;

    @CreationTimestamp
    private LocalDateTime orderPlaced;

    @Type(type="org.hibernate.type.UUIDCharType")
    private UUID productId;
    private int amount;

    @Type(type="org.hibernate.type.UUIDCharType")
    private UUID customerId;

    public void setOrderStatus(String orderStatus) {
        this.orderStatus = "Order placed";
    }
}
