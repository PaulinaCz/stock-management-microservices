package com.czerniecka.order.service;

import com.czerniecka.order.dto.OrderDTO;
import com.czerniecka.order.dto.OrderMapper;
import com.czerniecka.order.entity.Order;
import com.czerniecka.order.repository.OrderRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.runner.RunWith;
import org.mapstruct.factory.Mappers;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.web.client.RestTemplate;

import java.util.List;

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

}