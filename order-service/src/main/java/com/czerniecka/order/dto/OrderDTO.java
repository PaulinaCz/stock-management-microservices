package com.czerniecka.order.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Positive;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderDTO {

    @NotNull
    @Pattern(regexp = "^(MasterCard|Visa|AmericanExpress|mastercard|visa|americanexpress)$",
            message = "Please check which payment types are available")
    private String paymentType;
    private String orderStatus;

    @NotNull(message = "Please choose product")
    private String productId;

    @NotNull
    @Positive(message = "Order amount must be greater than 0")
    private int amount;

    @NotNull(message = "Customer Id must not be null")
    private String customerId;

}
