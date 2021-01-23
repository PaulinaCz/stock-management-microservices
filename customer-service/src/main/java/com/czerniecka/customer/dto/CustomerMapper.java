package com.czerniecka.customer.dto;

import com.czerniecka.customer.entity.Customer;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CustomerMapper {

    CustomerDTO toCustomerDTO(Customer customer);
    List<CustomerDTO> toCustomersDTOs(List<Customer> customers);
    Customer toCustomer (CustomerDTO customerDTO);
}
