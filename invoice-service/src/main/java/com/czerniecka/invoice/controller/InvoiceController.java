package com.czerniecka.invoice.controller;

import com.czerniecka.invoice.dto.InvoiceDTO;
import com.czerniecka.invoice.service.InvoiceService;
import com.czerniecka.invoice.vo.ResponseTemplateVO;
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
    public ResponseEntity<ResponseTemplateVO> getInvoiceWithProduct(@PathVariable UUID invoiceId){
        Optional<ResponseTemplateVO> withProduct = invoiceService.getInvoiceWithProduct(invoiceId);

        return withProduct.map(responseTemplateVO -> new ResponseEntity<>(responseTemplateVO, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PostMapping("")
    public ResponseEntity<InvoiceDTO> addInvoice(@RequestBody InvoiceDTO invoiceDTO){
        InvoiceDTO saved = invoiceService.save(invoiceDTO);
        return new ResponseEntity<>(saved, HttpStatus.CREATED);
    }

}
