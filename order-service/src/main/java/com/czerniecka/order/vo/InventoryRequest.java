package com.czerniecka.order.vo;

import com.czerniecka.order.dto.OrderDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InventoryRequest {

    private OrderDTO order;
    private Inventory inventory;
}
