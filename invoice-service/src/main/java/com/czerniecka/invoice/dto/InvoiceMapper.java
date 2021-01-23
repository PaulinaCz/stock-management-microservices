package com.czerniecka.invoice.dto;

import com.czerniecka.invoice.entity.Invoice;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface InvoiceMapper {

    InvoiceDTO toInvoiceDTO(Invoice invoice);
    List<InvoiceDTO> toInvoiceDTOs(List<Invoice> invoices);
    Invoice toInvoice(InvoiceDTO invoiceDTO);
}
