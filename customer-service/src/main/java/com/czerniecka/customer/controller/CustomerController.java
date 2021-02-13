package com.czerniecka.customer.controller;

import com.czerniecka.customer.dto.CustomerDTO;
import com.czerniecka.customer.service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

    @GetMapping("/{id}")
    public ResponseEntity<Mono<CustomerDTO>> getCustomerById(@PathVariable("id") String customerId){
        Mono<CustomerDTO> customer = customerService.findCustomerById(customerId);

        HttpStatus status = customer != null ? HttpStatus.OK : HttpStatus.NOT_FOUND;

        return new ResponseEntity<>(customer, status);
    }

    @PostMapping("")
    public ResponseEntity<Mono<CustomerDTO>> addCustomer(@RequestBody @Valid CustomerDTO customerDTO){
        Mono<CustomerDTO> saved = customerService.save(customerDTO);

        HttpStatus status = saved != null ? HttpStatus.CREATED : HttpStatus.BAD_REQUEST;

        return new ResponseEntity<>(saved, status);
    }

    //TODO
//    @PutMapping("/{customerId}")
//    public ResponseEntity<Void> updateCustomer(@PathVariable UUID customerId,
//                               @RequestBody @Valid CustomerDTO customerDTO){
//
//        if(!customerService.updateCustomer(customerId, customerDTO)){
//            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
//        }else{
//            return new ResponseEntity<>(HttpStatus.CREATED);
//        }
//    }

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

