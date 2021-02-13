package com.czerniecka.customer.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CustomerDTO {

    @NotBlank(message = "Please provide name")
    private String name;

    @NotBlank(message = "Email is required")
    @Email(message = "Email not valid")
    private String email;
}
