package com.czerniecka.order.dto;

import com.czerniecka.order.entity.Order;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface OrderMapper {

    OrderDTO toOrderDTO(Order order);
    List<OrderDTO> toOrdersDTOs(List<Order> orders);
    Order toOrder(OrderDTO orderDTO);
}
