package com.czerniecka.invoice.repository;

import com.czerniecka.invoice.entity.Invoice;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface InvoiceRepository extends ReactiveCrudRepository<Invoice, String> {
    
}
