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

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class InvoiceService {
    
    private final InvoiceRepository invoiceRepository;
    private final InvoiceMapper invoiceMapper;
    private RestTemplate restTemplate;
    private final ProductServiceClient productServiceClient;

    @Autowired
    public InvoiceService(InvoiceRepository invoiceRepository, InvoiceMapper invoiceMapper, RestTemplate restTemplate, ProductServiceClient productServiceClient) {
        this.invoiceRepository = invoiceRepository;
        this.invoiceMapper = invoiceMapper;
        this.restTemplate = restTemplate;
        this.productServiceClient = productServiceClient;
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
            Product product = productServiceClient.getProduct(invoice.getProductId());
            product.setId(invoice.getProductId());
            vo.setInvoice(invoiceMapper.toInvoiceDTO(invoice));
            vo.setProduct(product);

            return Optional.of(vo);
        }else{
            return Optional.empty();
        }
    }

    //TODO: POST NEEDS TO BE TRANSACTIONAL
    public InvoiceDTO save(InvoiceDTO invoiceDTO) {
        Invoice invoice = invoiceMapper.toInvoice(invoiceDTO);

        //update inventory -> adds purchased items to stock
        Inventory inventory = restTemplate.getForObject("http://inventory-service/inventory/product/" + invoice.getProductId()
                                                        ,Inventory.class);
        inventory.setQuantity(inventory.getQuantity() + invoice.getAmount());
        restTemplate.put("http://inventory-service/inventory/inventory/" + inventory.getId(), inventory, Inventory.class);
        Invoice saved = invoiceRepository.save(invoice);

        return invoiceMapper.toInvoiceDTO(saved);
    }
}
