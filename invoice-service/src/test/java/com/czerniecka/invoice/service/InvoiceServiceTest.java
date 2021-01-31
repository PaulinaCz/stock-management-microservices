package com.czerniecka.invoice.service;

import com.czerniecka.invoice.dto.InvoiceDTO;
import com.czerniecka.invoice.dto.InvoiceMapper;
import com.czerniecka.invoice.entity.Invoice;
import com.czerniecka.invoice.repository.InvoiceRepository;
import com.czerniecka.invoice.vo.Inventory;
import com.czerniecka.invoice.vo.InvoiceProductResponse;
import com.czerniecka.invoice.vo.Product;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mapstruct.factory.Mappers;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class InvoiceServiceTest {

    @Mock
    InvoiceRepository invoiceRepository;
    @Spy
    InvoiceMapper invoiceMapper = Mappers.getMapper(InvoiceMapper.class);
    @Mock
    RestTemplate restTemplate;

    InvoiceService invoiceService;

    @Before
    public void init(){
        invoiceService = new InvoiceService(invoiceRepository, invoiceMapper, restTemplate);
    }

    @Test
    public void shouldReturnAllInvoices(){

        when(invoiceRepository.findAll()).thenReturn(List.of(new Invoice(), new Invoice(), new Invoice()));
        List<InvoiceDTO> all = invoiceService.findAll();
        assertEquals(3, all.size());
    }

    @Test
    public void shouldReturnInvoice(){

        UUID invoiceId = UUID.randomUUID();
        Invoice invoice = new Invoice(invoiceId, UUID.randomUUID(), 10, LocalDateTime.now(), UUID.randomUUID());
        Product product = new Product();

        when(invoiceRepository.findById(UUID.randomUUID())).thenReturn(Optional.of(invoice));
        when(restTemplate.getForObject("http://product-service/products/" + UUID.randomUUID(),
                Product.class)).thenReturn(product);

        Optional<InvoiceProductResponse> i = invoiceService.getInvoiceWithProduct(invoiceId);

        assertEquals(Optional.of(new InvoiceProductResponse(invoiceMapper.toInvoiceDTO(invoice), product)), i);

    }

    @Test
    public void shouldReturnSavedInvoice(){

        UUID invoiceId = UUID.randomUUID();
        UUID productId = UUID.randomUUID();
        Invoice invoice = new Invoice(invoiceId, productId, 10, LocalDateTime.now(), UUID.randomUUID());

        Inventory inventory = new Inventory();
        inventory.setId(UUID.randomUUID());
        inventory.setLastModified(LocalDateTime.now());
        inventory.setProductId(invoice.getProductId());
        inventory.setQuantity(2);


        when(restTemplate.getForObject("http://inventory-service/inventory/product/" + invoice.getProductId()
                , Inventory.class)).thenReturn(inventory);
        when(invoiceRepository.save(any(Invoice.class))).thenReturn(invoice);

        InvoiceDTO saved = invoiceService.save(invoiceMapper.toInvoiceDTO(invoice));

        assertThat(saved.getAmount()).isEqualTo(10);
        assertThat(saved.getId()).isEqualTo(invoiceId);

    }
}