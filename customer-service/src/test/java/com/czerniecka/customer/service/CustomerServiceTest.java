package com.czerniecka.customer.service;

import com.czerniecka.customer.dto.CustomerDTO;
import com.czerniecka.customer.dto.CustomerMapper;
import com.czerniecka.customer.entity.Customer;
import com.czerniecka.customer.repository.CustomerRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mapstruct.factory.Mappers;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class CustomerServiceTest {

    @Mock
    CustomerRepository customerRepository;
    @Spy
    CustomerMapper customerMapper = Mappers.getMapper(CustomerMapper.class);

    CustomerService customerService;

    @Before
    public void init(){
        customerService = new CustomerService(customerRepository, customerMapper);
    }

    @Test
    public void shouldMapCustomerToCustomerDTO(){

        Customer customer = new Customer(UUID.randomUUID(), "Customer", "customer@gmail.com");
        CustomerDTO customerDTO = customerMapper.toCustomerDTO(customer);

        assertThat(customerDTO.getName().equals("Customer"));
        assertThat(customerDTO.getEmail().equals("customer@gmail.com"));
    }

    @Test
    public void shouldReturnCustomer(){

        UUID customerId = UUID.randomUUID();
        Customer customer = new Customer(customerId, "Customer", "customer@gmail.com");

        when(customerRepository.save(new Customer())).thenReturn(customer);
        Optional<CustomerDTO> customerById = customerService.findCustomerById(customerId);

        assertEquals(Optional.of(customerMapper.toCustomerDTO(customer)), customerById);

    }
}
