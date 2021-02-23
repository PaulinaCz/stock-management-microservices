package com.czerniecka.customer.dto;

import com.czerniecka.customer.entity.Customer;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CustomerMapper {

    CustomerDTO toCustomerDTO(Customer customer);
    Customer toCustomer (CustomerDTO customerDTO);
}
