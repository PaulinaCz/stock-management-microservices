package com.czerniecka.customer.service;

import com.czerniecka.customer.controller.NotFoundException;
import com.czerniecka.customer.dto.CustomerDTO;
import com.czerniecka.customer.dto.CustomerMapper;
import com.czerniecka.customer.entity.Customer;
import com.czerniecka.customer.repository.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class CustomerService {

    private final CustomerRepository customerRepository;
    private final CustomerMapper customerMapper;

    @Autowired
    public CustomerService(CustomerRepository customerRepository, CustomerMapper customerMapper) {
        this.customerRepository = customerRepository;
        this.customerMapper = customerMapper;
    }

    public List<CustomerDTO> findAll() {

        List<Customer> all = customerRepository.findAll();
        return customerMapper.toCustomersDTOs(all);
    }

    public CustomerDTO findById(UUID customerId) {

        Optional<Customer> byId = customerRepository.findById(customerId);

        return byId.map(customerMapper::toCustomerDTO).orElse(null);
    }

    public CustomerDTO save(CustomerDTO customerDTO) {

        Customer customer = customerMapper.toCustomer(customerDTO);
        Customer saved = customerRepository.save(customer);
        return customerMapper.toCustomerDTO(saved);
    }

    public void updateCustomer(UUID customerId, CustomerDTO customerDTO) {

        Customer c = customerRepository.findById(customerId).orElseThrow(NotFoundException::new);

        c.setName(customerDTO.getName());
        c.setEmail(customerDTO.getEmail());

        customerRepository.save(c);

    }
}