package com.czerniecka.invoice.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

@Data
public class InvoiceDTO {

    private String id;

    @NotNull(message = "Please choose product")
    private String productId;

    @NotNull
    @Positive(message = "Invoice amount must be greater than 0")
    private int amount;
    @NotNull(message = "Supplier Id must not be null")
    private String supplierId;

}
