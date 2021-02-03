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
    private final ProductServiceClient productServiceClient;
    private final InventoryServiceClient inventoryServiceClient;

    @Autowired
    public InvoiceService(InvoiceRepository invoiceRepository, InvoiceMapper invoiceMapper, ProductServiceClient productServiceClient, InventoryServiceClient inventoryServiceClient) {
        this.invoiceRepository = invoiceRepository;
        this.invoiceMapper = invoiceMapper;
        this.productServiceClient = productServiceClient;
        this.inventoryServiceClient = inventoryServiceClient;
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

    public Optional<InvoiceDTO> save(InvoiceDTO invoiceDTO) {
        Invoice invoice = invoiceMapper.toInvoice(invoiceDTO);

        //update inventory -> adds purchased items to stock
        Inventory inventory = inventoryServiceClient.getInventory(invoice.getProductId());
        inventory.setQuantity(inventory.getQuantity() + invoice.getAmount());
        inventoryServiceClient.putInventory(inventory);
        if(inventory.getId()!=null){
            Invoice saved = invoiceRepository.save(invoice);
            return Optional.of(invoiceMapper.toInvoiceDTO(saved));
        }else{
            return Optional.empty();
        }

    }
}
