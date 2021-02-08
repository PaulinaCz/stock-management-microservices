package com.czerniecka.order.vo;

import com.czerniecka.order.dto.OrderDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderWithProductResponse {

    private OrderDTO order;
    private Product product;
}
