package com.czerniecka.product.vo;

import com.czerniecka.product.entity.Product;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResponseTemplateVO {

    private Product product;
    private Supplier supplier;
}
