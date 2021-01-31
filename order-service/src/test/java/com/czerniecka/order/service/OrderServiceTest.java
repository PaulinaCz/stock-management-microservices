package com.czerniecka.order.service;

import com.czerniecka.order.dto.OrderDTO;
import com.czerniecka.order.dto.OrderMapper;
import com.czerniecka.order.entity.Order;
import com.czerniecka.order.repository.OrderRepository;
import com.czerniecka.order.vo.Inventory;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.runner.RunWith;
import org.mapstruct.factory.Mappers;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;


@RunWith(MockitoJUnitRunner.class)
public class OrderServiceTest {

    @Mock
    OrderRepository orderRepository;
    @Spy
    OrderMapper orderMapper = Mappers.getMapper(OrderMapper.class);
    @Mock
    RestTemplate restTemplate;

    OrderService orderService;

    @Before
    public void init() {
        orderService = new OrderService(orderRepository, orderMapper, restTemplate);
    }

    @Test
    public void shouldReturnAllOrders() {
        when(orderRepository.findAll()).thenReturn(List.of(
                new Order(),
                new Order(),
                new Order(),
                new Order(),
                new Order()
        ));

        List<OrderDTO> all = orderService.findAll();
        Assertions.assertEquals(5, all.size());
    }


    @Test
    public void shouldReturnSavedObject(){

        Order order = new Order(UUID.randomUUID(), "cash", "shipped", LocalDateTime.now(),
                UUID.randomUUID(), 5, UUID.randomUUID());

        Inventory inventory = new Inventory();
        inventory.setId(UUID.randomUUID());
        inventory.setLastModified(LocalDateTime.now());
        inventory.setProductId(order.getProductId());
        inventory.setQuantity(15);

        when(orderRepository.save(new Order())).thenReturn(order);
        when(restTemplate.getForObject("http://inventory-service/inventory/product/" + order.getProductId()
                ,Inventory.class)).thenReturn(inventory);

        OrderDTO saved = orderService.save(orderMapper.toOrderDTO(order));

        assertThat(saved.getAmount()).isEqualTo(5);
        assertThat(saved.getOrderStatus()).isEqualTo("shipped");

    }

}