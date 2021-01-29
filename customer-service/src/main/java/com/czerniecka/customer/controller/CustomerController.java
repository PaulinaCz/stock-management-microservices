package com.czerniecka.customer.controller;

import com.czerniecka.customer.dto.CustomerDTO;
import com.czerniecka.customer.service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<List<CustomerDTO>> getAllCustomers(){
        List<CustomerDTO> all = customerService.findAll();
        return ResponseEntity.ok(all);
    }

    @GetMapping("/{customerId}")
    public ResponseEntity getCustomerById(@PathVariable UUID customerId){
        Optional<CustomerDTO> customer = customerService.findCustomerById(customerId);

        return customer.map(customerDTO -> new ResponseEntity(customerDTO, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity(HttpStatus.NOT_FOUND));
    }

    @PostMapping("")
    public ResponseEntity<CustomerDTO> addCustomer(@RequestBody CustomerDTO customerDTO){
        CustomerDTO saved = customerService.save(customerDTO);
        return new ResponseEntity<>(saved, HttpStatus.CREATED);
    }

    @PutMapping("/customer/{customerId}")
    public ResponseEntity<Void> updateCustomer(@PathVariable UUID customerId,
                               @RequestBody CustomerDTO customerDTO){

        if(!customerService.updateCustomer(customerId, customerDTO)){
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }else{
            return new ResponseEntity<>(HttpStatus.CREATED);
        }
    }
}

