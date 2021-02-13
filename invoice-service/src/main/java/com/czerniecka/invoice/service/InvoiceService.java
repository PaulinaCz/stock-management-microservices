package com.czerniecka.invoice.service;

import com.czerniecka.invoice.client.InventoryServiceClient;
import com.czerniecka.invoice.client.ProductServiceClient;
import com.czerniecka.invoice.dto.InvoiceDTO;
import com.czerniecka.invoice.dto.InvoiceMapper;
import com.czerniecka.invoice.entity.Invoice;
import com.czerniecka.invoice.repository.InvoiceRepository;
import com.czerniecka.invoice.vo.Inventory;
import com.czerniecka.invoice.vo.Product;
import com.czerniecka.invoice.vo.InvoiceProductResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

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
    public InvoiceService(InvoiceRepository invoiceRepository, InvoiceMapper invoiceMapper,
                          ProductServiceClient productServiceClient, InventoryServiceClient inventoryServiceClient) {
        this.invoiceRepository = invoiceRepository;
        this.invoiceMapper = invoiceMapper;
        this.productServiceClient = productServiceClient;
        this.inventoryServiceClient = inventoryServiceClient;
    }


    public List<InvoiceDTO> findAll() {

        List<Invoice> all = invoiceRepository.findAll();
        return invoiceMapper.toInvoiceDTOs(all);
    }
    
    /* If product-service is unavailable, returns Invoice with empty Product object*/    
    public Optional<InvoiceProductResponse> getInvoiceWithProduct(UUID invoiceId) {
        InvoiceProductResponse response = new InvoiceProductResponse();

        Optional<Invoice> i = invoiceRepository.findById(invoiceId);

        if (i.isPresent()) {
            Invoice invoice = i.get();
            Product product = productServiceClient.getProduct(invoice.getProductId());
            product.setId(invoice.getProductId());
            response.setInvoice(invoiceMapper.toInvoiceDTO(invoice));
            response.setProduct(product);

            return Optional.of(response);
        } else {
            return Optional.empty();
        }
    }

    public Optional<InvoiceDTO> save(InvoiceDTO invoiceDTO) {
        Invoice invoice = invoiceMapper.toInvoice(invoiceDTO);
        Invoice saved = invoiceRepository.save(invoice);
         /*If inventory-service is not available, service client will invoke fallback method and
        will stop executing rest of POST order*/
        Inventory inventory = inventoryServiceClient.getInventory(saved.getProductId());
        
        /* Update inventory -> adds purchased items to stock */
        inventory.setQuantity(inventory.getQuantity() + invoice.getAmount());
        HttpStatus httpStatus = inventoryServiceClient.putInventory(inventory);
        if (httpStatus.equals(HttpStatus.CREATED)) {
            return Optional.of(invoiceMapper.toInvoiceDTO(saved));
        } else {
            invoiceRepository.delete(saved);
            return Optional.empty();
        }
    }
}
