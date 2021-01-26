package com.czerniecka.product.vo;

import com.czerniecka.product.dto.ProductDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InventoryRequest {
    private Inventory inventory;
    private ProductDTO product;
}
