package com.czerniecka.supplier.controller;

import com.czerniecka.supplier.dto.SupplierDTO;
import com.czerniecka.supplier.exceptions.SupplierNotFound;
import com.czerniecka.supplier.service.SupplierService;
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
@RequestMapping("/suppliers")
public class SupplierController {

    private final SupplierService supplierService;

    @Autowired
    public SupplierController(SupplierService supplierService) {
        this.supplierService = supplierService;
    }

    @GetMapping(value = "", produces = "application/json")
    public Flux<SupplierDTO> getAllSuppliers(){
        return supplierService.findAll();
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/{id}")
    public Mono<SupplierDTO> getSupplierById(@PathVariable("id") String supplierId){
        
        return supplierService.findSupplierById(supplierId)
                .switchIfEmpty(Mono.error(new SupplierNotFound(supplierId)));
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("")
    public Mono<SupplierDTO> addSupplier(@RequestBody @Valid SupplierDTO supplierDTO){
        return supplierService.save(supplierDTO);
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PutMapping("/{id}")
    public Mono<SupplierDTO> updateSupplier(@PathVariable("id") String supplierId,
                               @RequestBody @Valid SupplierDTO supplierDTO){

        return supplierService.updateSupplier(supplierId, supplierDTO)
                .switchIfEmpty(Mono.error(new SupplierNotFound(supplierId)));
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(SupplierNotFound.class)
    public Map<String, Object> handleNotFound(SupplierNotFound ex){

        Map<String, Object> errorBody = new HashMap<>();

        errorBody.put("timestamp", LocalDateTime.now());
        errorBody.put("error", "Supplier " + ex.getMessage() + " not found");

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
