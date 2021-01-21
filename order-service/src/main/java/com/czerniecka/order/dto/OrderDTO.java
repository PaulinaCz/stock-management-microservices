package com.czerniecka.order.dto;

import lombok.Data;

import java.util.UUID;

@Data
public class OrderDTO {

    private String paymentType;
    private String orderStatus;
    private UUID productId;
    private int amount;
    private UUID customerId;

}
