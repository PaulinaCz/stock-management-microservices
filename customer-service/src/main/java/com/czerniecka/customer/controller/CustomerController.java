package com.czerniecka.customer.controller;

import com.czerniecka.customer.dto.CustomerDTO;
import com.czerniecka.customer.exceptions.CustomerNotFound;
import com.czerniecka.customer.service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/customers")
public class CustomerController {

    private final CustomerService customerService;

    @Autowired
    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @GetMapping(value = "", produces = "application/json")
    public Flux<CustomerDTO> getAllCustomers(){
        return customerService.findAll();
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/{id}")
    public Mono<CustomerDTO> getCustomerById(@PathVariable("id") String customerId){

        return customerService.findCustomerById(customerId)
                .switchIfEmpty(Mono.error(new CustomerNotFound(customerId)));
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("")
    public Mono<CustomerDTO>addCustomer(@RequestBody @Valid CustomerDTO customerDTO){
        return customerService.save(customerDTO);
    }


    @ResponseStatus(HttpStatus.CREATED)
    @PutMapping("/{id}")
    public Mono<CustomerDTO> updateCustomer(@PathVariable("id") String customerId,
                               @RequestBody @Valid CustomerDTO customerDTO){

        return customerService.updateCustomer(customerId, customerDTO)
                .switchIfEmpty(Mono.error(new CustomerNotFound(customerId)));

    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(CustomerNotFound.class)
    public Map<String, Object> handleNotFound(CustomerNotFound ex){

        Map<String, Object> errorBody = new HashMap<>();

        errorBody.put("timestamp", LocalDateTime.now());
        errorBody.put("error", "Customer " + ex.getMessage() + " not found");

        return errorBody;
    }

    //TODO: fix input validation & response
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Map<String, Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex) {

        Map<String, Object> errorBody = new HashMap<>();

        errorBody.put("timestamp", LocalDateTime.now());

        List<String> errors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .collect(Collectors.toList());

        errorBody.put("validationErrors", errors);
        return errorBody;
    }
}

