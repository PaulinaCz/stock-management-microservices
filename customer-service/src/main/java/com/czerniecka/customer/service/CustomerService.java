package com.czerniecka.customer.service;

import com.czerniecka.customer.controller.NotFoundException;
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

    @Autowired
    public CustomerService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    public List<Customer> findAll() {

        return customerRepository.findAll();
    }

    public Optional<Customer> findById(UUID customerId) {

        return customerRepository.findById(customerId);
    }

    public Customer save(Customer customer) {

        return customerRepository.save(customer);
    }

    public void updateCustomer(UUID customerId, Customer customer) {

        Customer c = customerRepository.findById(customerId).orElseThrow(NotFoundException::new);

        c.setName(customer.getName());
        c.setEmail(customer.getEmail());

        customerRepository.save(c);

    }
}