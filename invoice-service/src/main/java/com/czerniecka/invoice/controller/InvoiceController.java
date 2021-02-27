package com.czerniecka.invoice.controller;

import com.czerniecka.invoice.dto.InvoiceDTO;
import com.czerniecka.invoice.exceptions.InvoiceNotFound;
import com.czerniecka.invoice.service.InvoiceService;
import com.czerniecka.invoice.vo.InvoiceProductResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.naming.ServiceUnavailableException;
import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/invoices")
public class InvoiceController {
    
    private final InvoiceService invoiceService;

    @Autowired
    public InvoiceController(InvoiceService invoiceService) {
        this.invoiceService = invoiceService;
    }
    
    @GetMapping("")
    public Flux<InvoiceDTO> getAllInvoices(){
        return invoiceService.findAll();
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/{id}")
    public Mono<InvoiceProductResponse> getInvoiceWithProduct(@PathVariable("id") String invoiceId){
        
        return invoiceService.getInvoiceWithProduct(invoiceId)
                .switchIfEmpty(Mono.error(new InvoiceNotFound(invoiceId)));
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("")
    public Mono<InvoiceDTO> addInvoice(@RequestBody @Valid InvoiceDTO invoiceDTO){
        
        return invoiceService.save(invoiceDTO);
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(InvoiceNotFound.class)
    public Map<String, Object> handleNotFound(InvoiceNotFound e){

        Map<String, Object> errorBody = new HashMap<>();

        errorBody.put("timestamp", LocalDateTime.now());
        errorBody.put("error", "Invoice " + e.getMessage() + " not found");

        return errorBody;
    }

    @ResponseStatus(HttpStatus.SERVICE_UNAVAILABLE)
    @ExceptionHandler(ServiceUnavailableException.class)
    public Map<String, Object> handleNotSaved(ServiceUnavailableException e){

        Map<String, Object> errorBody = new HashMap<>();

        errorBody.put("timestamp", LocalDateTime.now());
        errorBody.put("error", e.getMessage());

        return errorBody;
    }

    //TODO: fix input validation & exception handler

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
