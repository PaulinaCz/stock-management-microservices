package com.czerniecka.customer.controller;

import com.czerniecka.customer.dto.CustomerDTO;
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
    public List<CustomerDTO> getAllCustomers(){

        return customerService.findAll();
    }

    @GetMapping("/{customerId}")
    public CustomerDTO getCustomerById(@PathVariable UUID customerId){

        return customerService.findById(customerId);

    }

    @PostMapping("")
    public CustomerDTO addCustomer(@RequestBody CustomerDTO customerDTO){

        return customerService.save(customerDTO);
    }

    @PutMapping("/customer/{customerId}")
    public void updateCustomer(@PathVariable UUID customerId,
                               @RequestBody CustomerDTO customerDTO){

        customerService.updateCustomer(customerId, customerDTO);
    }
}

