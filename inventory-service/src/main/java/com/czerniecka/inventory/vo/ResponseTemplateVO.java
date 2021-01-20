package com.czerniecka.inventory.vo;

import com.czerniecka.inventory.entity.Inventory;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResponseTemplateVO {

    private Inventory inventory;
    private Product product;
}
