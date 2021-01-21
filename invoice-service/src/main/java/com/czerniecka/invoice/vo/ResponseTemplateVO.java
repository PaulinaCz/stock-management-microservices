package com.czerniecka.invoice.vo;

import com.czerniecka.invoice.entity.Invoice;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResponseTemplateVO {
    
    private Invoice invoice;
    private Product product;
}
