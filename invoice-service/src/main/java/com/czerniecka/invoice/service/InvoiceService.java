package com.czerniecka.invoice.service;

import com.czerniecka.invoice.entity.Invoice;
import com.czerniecka.invoice.repository.InvoiceRepository;
import com.czerniecka.invoice.vo.Product;
import com.czerniecka.invoice.vo.ResponseTemplateVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.UUID;

@Service
public class InvoiceService {
    
    private final InvoiceRepository invoiceRepository;
    private RestTemplate restTemplate;

    @Autowired
    public InvoiceService(InvoiceRepository invoiceRepository, RestTemplate restTemplate) {
        this.invoiceRepository = invoiceRepository;
        this.restTemplate = restTemplate;
    }

    public List<Invoice> findAll() {
        
        return invoiceRepository.findAll();
    }

    public ResponseTemplateVO getInvoiceWithProduct(UUID invoiceId) {
        ResponseTemplateVO vo = new ResponseTemplateVO();
        
        Invoice invoice = invoiceRepository.findInvoiceById(invoiceId);
        Product product = restTemplate.getForObject("http://localhost:3001/products/" + invoice.getProductId(),
                                                    Product.class);
        
        vo.setInvoice(invoice);
        vo.setProduct(product);
        
        return vo;
        
    }

    public Invoice save(Invoice invoice) {
        
        return invoiceRepository.save(invoice);
    }
}
