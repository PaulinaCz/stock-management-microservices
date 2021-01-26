package com.czerniecka.invoice.service;

import com.czerniecka.invoice.dto.InvoiceDTO;
import com.czerniecka.invoice.dto.InvoiceMapper;
import com.czerniecka.invoice.entity.Invoice;
import com.czerniecka.invoice.repository.InvoiceRepository;
import com.czerniecka.invoice.vo.Inventory;
import com.czerniecka.invoice.vo.Product;
import com.czerniecka.invoice.vo.InvoiceProductResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class InvoiceService {
    
    private final InvoiceRepository invoiceRepository;
    private final InvoiceMapper invoiceMapper;
    private RestTemplate restTemplate;

    @Autowired
    public InvoiceService(InvoiceRepository invoiceRepository, InvoiceMapper invoiceMapper, RestTemplate restTemplate) {
        this.invoiceRepository = invoiceRepository;
        this.invoiceMapper = invoiceMapper;
        this.restTemplate = restTemplate;
    }

    public List<InvoiceDTO> findAll() {

        List<Invoice> all = invoiceRepository.findAll();
        return invoiceMapper.toInvoiceDTOs(all);
    }

    public Optional<InvoiceProductResponse> getInvoiceWithProduct(UUID invoiceId) {
        InvoiceProductResponse vo = new InvoiceProductResponse();
        
        Optional<Invoice> i = invoiceRepository.findById(invoiceId);

        if(i.isPresent()){
            Invoice invoice = i.get();
            Product product = restTemplate.getForObject("http://localhost:3001/products/" + invoice.getProductId(),
                    Product.class);

            product.setId(invoice.getProductId());
            vo.setInvoice(invoiceMapper.toInvoiceDTO(invoice));
            vo.setProduct(product);

            return Optional.of(vo);
        }else{
            return Optional.empty();
        }
    }

    public InvoiceDTO save(InvoiceDTO invoiceDTO) {
        Invoice invoice = invoiceMapper.toInvoice(invoiceDTO);

        //update inventory -> adds purchased items to stock
        Inventory inventory = restTemplate.getForObject("http://localhost:3005/inventory/product/" + invoice.getProductId()
                                                        ,Inventory.class);
        inventory.setQuantity(inventory.getQuantity() + invoice.getAmount());
        restTemplate.put("http://localhost:3005/inventory/inventory/" + inventory.getId(), inventory, Inventory.class);
        Invoice saved = invoiceRepository.save(invoice);

        return invoiceMapper.toInvoiceDTO(saved);
    }
}
