package com.czerniecka.invoice.controller;

import com.czerniecka.invoice.dto.InvoiceDTO;
import com.czerniecka.invoice.service.InvoiceService;
import com.czerniecka.invoice.vo.InvoiceProductResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/invoices")
public class InvoiceController {
    
    private final InvoiceService invoiceService;

    @Autowired
    public InvoiceController(InvoiceService invoiceService) {
        this.invoiceService = invoiceService;
    }
    
    @GetMapping("")
    public ResponseEntity<List<InvoiceDTO>> getAllInvoices(){
        List<InvoiceDTO> all = invoiceService.findAll();
        return ResponseEntity.ok(all);
    }

    @GetMapping("/{invoiceId}")
    public ResponseEntity<InvoiceProductResponse> getInvoiceWithProduct(@PathVariable UUID invoiceId){
        Optional<InvoiceProductResponse> withProduct = invoiceService.getInvoiceWithProduct(invoiceId);

        return withProduct.map(invoiceProductResponse -> new ResponseEntity<>(invoiceProductResponse, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PostMapping("")
    public ResponseEntity<InvoiceDTO> addInvoice(@RequestBody InvoiceDTO invoiceDTO){
        Optional<InvoiceDTO> saved = invoiceService.save(invoiceDTO);
        return saved.map(invoice -> new ResponseEntity<>(invoice, HttpStatus.CREATED))
                .orElseGet(() -> new ResponseEntity("Error while processing invoice, please try again later.",
                        HttpStatus.SERVICE_UNAVAILABLE));
    }

}
