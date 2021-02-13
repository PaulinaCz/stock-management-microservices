package com.czerniecka.supplier.dto;

import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Data
public class SupplierDTO {

    @NotBlank(message = "Please provide name")
    private String name;

    @NotBlank(message = "Email is required")
    @Email(message = "Email not valid")
    private String email;
}
