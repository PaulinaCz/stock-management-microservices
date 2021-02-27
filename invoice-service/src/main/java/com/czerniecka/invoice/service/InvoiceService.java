package com.czerniecka.invoice.service;

import com.czerniecka.invoice.client.InventoryServiceClient;
import com.czerniecka.invoice.client.ProductServiceClient;
import com.czerniecka.invoice.dto.InvoiceDTO;
import com.czerniecka.invoice.dto.InvoiceMapper;
import com.czerniecka.invoice.entity.Invoice;
import com.czerniecka.invoice.repository.InvoiceRepository;
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
     * On save Invoice i Inventory for i product should be updated
     *
     * On successful inventory update -> Invoice i is saved to db
     * else -> save method on InvoiceRepository is never invoked
     * and error is thrown.
     *
     */
    public Mono<InvoiceDTO> save(InvoiceDTO invoiceDTO) {
        
        var invoice = invoiceMapper.toInvoice(invoiceDTO);
        var inventoryMono = inventoryServiceClient.updateInventory(invoiceDTO);
        
        return inventoryMono.switchIfEmpty(Mono.empty())
                .flatMap(i -> invoiceRepository.save(invoice).map(invoiceMapper::toInvoiceDTO));

    }
}
