package com.czerniecka.order.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Positive;
import java.util.UUID;

@Data
public class OrderDTO {

    @NotNull
    @Pattern(regexp = "^(MasterCard|Visa|AmericanExpress|mastercard|visa|americanexpress)$",
            message = "Please check which payment types are available")
    private String paymentType;
    private String orderStatus;

    @NotNull(message = "Please choose product")
    private UUID productId;

    @NotNull
    @Positive(message = "Order amount must be greater than 0")
    private int amount;

    @NotNull(message = "Customer Id must not be null")
    private UUID customerId;

}
