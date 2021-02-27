package com.czerniecka.invoice.service;

import com.czerniecka.invoice.client.InventoryServiceClient;
import com.czerniecka.invoice.client.ProductServiceClient;
import com.czerniecka.invoice.dto.InvoiceDTO;
import com.czerniecka.invoice.dto.InvoiceMapper;
import com.czerniecka.invoice.entity.Invoice;
import com.czerniecka.invoice.repository.InvoiceRepository;
import com.czerniecka.invoice.vo.Inventory;
import com.czerniecka.invoice.vo.InvoiceProductResponse;
import org.springframework.beans.factory.annotation.Autowired;
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

    /**
     *  If product-service is not available method returns:
     *  Mono of InvoiceProductResponse
     *  with Invoice object and empty Product object
     *
     *  If invoice is not found returns empty Mono
     *  which will return "Invoice not found error" in controller
     */
    public Mono<InvoiceProductResponse> getInvoiceWithProduct(String invoiceId) {
        var invoice= invoiceRepository.findById(invoiceId);

        return invoice.switchIfEmpty(Mono.empty())
                .flatMap(i -> productServiceClient.getProduct(i.getProductId(), invoiceMapper.toInvoiceDTO(i)));

    }

    /**
     * If inventory-service is not available while calling on GET/PUT method - fallback method will return empty Inventory object.
     * If save method receives empty Invoice object, it reverses save invoice to database.
     *
     * If PUT method on inventory-service is successful - both database changes are committed.
     */
    public Mono<InvoiceDTO> save(InvoiceDTO invoiceDTO) {
        
        Invoice invoice = invoiceMapper.toInvoice(invoiceDTO);

        Mono<Inventory> inventoryMono = inventoryServiceClient.updateInventory(invoiceDTO);
        
        return inventoryMono.switchIfEmpty(Mono.empty())
                .flatMap(i -> invoiceRepository.save(invoice).map(invoiceMapper::toInvoiceDTO));

    }
}
