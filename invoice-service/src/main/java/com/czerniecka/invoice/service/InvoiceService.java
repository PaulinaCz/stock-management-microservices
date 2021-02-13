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
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;


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


    public Flux<InvoiceDTO> findAll() {

        Flux<Invoice> all = invoiceRepository.findAll();
        return all.map(invoiceMapper::toInvoiceDTO);
    }

    /* If product-service is unavailable, returns Invoice with empty Product object*/
    public Mono<InvoiceProductResponse> getInvoiceWithProduct(String invoiceId) {
        InvoiceProductResponse response = new InvoiceProductResponse();

        Mono<Invoice> invoice = invoiceRepository.findById(invoiceId);

        return invoice.flatMap(i -> {
                    Product product = productServiceClient.getProduct(i.getProductId());
                    product.setId(i.getProductId());
                    response.setInvoice(invoiceMapper.toInvoiceDTO(i));
                    response.setProduct(product);
                    return Mono.just(response);
                }
        ).or(Mono.empty());

    }

    public Mono<InvoiceDTO> save(InvoiceDTO invoiceDTO) {
        Invoice invoice = invoiceMapper.toInvoice(invoiceDTO);
        Mono<Invoice> saved = invoiceRepository.save(invoice);
         /*If inventory-service is not available, service client will invoke fallback method and
        will stop executing rest of POST order*/
        
        return saved.flatMap(i -> {
            Inventory inventory = inventoryServiceClient.getInventory(i.getProductId());
            inventory.setQuantity(inventory.getQuantity() + invoice.getAmount());
            HttpStatus httpStatus = inventoryServiceClient.putInventory(inventory);
            
            if (httpStatus.equals(HttpStatus.CREATED)) {
                return saved.map(invoiceMapper::toInvoiceDTO);
            } else {
                invoiceRepository.delete(i);
                return Mono.empty();
            }
        }).or(Mono.empty());
        
        
    }
}
