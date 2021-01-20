package com.czerniecka.customer.controller;

import com.czerniecka.customer.entity.Customer;
import com.czerniecka.customer.service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/customers")
public class CustomerController {

    private final CustomerService customerService;

    @Autowired
    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @GetMapping("")
    public List<Customer> getAllCustomers(){

        return customerService.findAll();
    }

    @GetMapping("/{customerId}")
    public Customer getCustomerById(@PathVariable UUID customerId){

        Optional<Customer> customerById = customerService.findById(customerId);

        return customerById.orElse(null);
    }

    @PostMapping("")
    public Customer addCustomer(@RequestBody Customer customer){

        return customerService.save(customer);
    }

    @PutMapping("/customer/{customerId}")
    public void updateCustomer(@PathVariable UUID customerId,
                               @RequestBody Customer customer){

        customerService.updateCustomer(customerId, customer);
    }
}

