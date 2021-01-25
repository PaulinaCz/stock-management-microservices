package com.czerniecka.inventory.vo;

import com.czerniecka.inventory.dto.InventoryDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResponseTemplateVO {

    private InventoryDTO inventory;
    private Product product;
}
