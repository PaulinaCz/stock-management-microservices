package com.czerniecka.invoice.controller;

import com.czerniecka.invoice.dto.InvoiceDTO;
import com.czerniecka.invoice.service.InvoiceService;
import com.czerniecka.invoice.vo.ResponseTemplateVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
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
    public List<InvoiceDTO> getAllInvoices(){
        
        return invoiceService.findAll();
    }

    @GetMapping("/{invoiceId}")
    public ResponseTemplateVO getInvoiceWithProduct(@PathVariable UUID invoiceId){

        return invoiceService.getInvoiceWithProduct(invoiceId);
    }

    @PostMapping("")
    public InvoiceDTO addInvoice(@RequestBody InvoiceDTO invoiceDTO){
        return invoiceService.save(invoiceDTO);
    }

}
