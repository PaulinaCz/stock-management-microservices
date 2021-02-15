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
     *  Invoice with empty Product object
     *
     *  If invoice is not found returns Mono.empty
     */
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

    /**
     * If inventory-service is not available while calling on GET/PUT method - fallback method will return empty Inventory object.
     * If save method receives empty Invoice object, it reverses save invoice to database.
     *
     * If PUT method on inventory-service is successful - both database changes are committed.
     */
    public Mono<InvoiceDTO> save(InvoiceDTO invoiceDTO) {
        Invoice invoice = invoiceMapper.toInvoice(invoiceDTO);
        Mono<Invoice> saved = invoiceRepository.save(invoice);

        return saved.flatMap(i -> {
            Inventory inventory = inventoryServiceClient.getInventory(i.getProductId());
            if(inventory.getId() == null){
                invoiceRepository.delete(i);
                return Mono.empty();
            }else{
                inventory.setQuantity(inventory.getQuantity() + i.getAmount());
                Inventory inventoryUpdated = inventoryServiceClient.putInventory(inventory);
                if(inventoryUpdated.getId() == null){
                    invoiceRepository.delete(i);
                    return Mono.empty();
                }else{
                    return Mono.just(invoiceMapper.toInvoiceDTO(i));
                }
            }
        });
    }
}
