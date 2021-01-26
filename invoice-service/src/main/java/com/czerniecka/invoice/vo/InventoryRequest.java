package com.czerniecka.invoice.vo;

import com.czerniecka.invoice.dto.InvoiceDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InventoryRequest {

    private InvoiceDTO invoice;
    private Inventory inventory;
}
