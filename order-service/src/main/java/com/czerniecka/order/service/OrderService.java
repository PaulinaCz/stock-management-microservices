package com.czerniecka.order.service;

import com.czerniecka.order.entity.Order;
import com.czerniecka.order.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class OrderService {

    private final OrderRepository orderRepository;

    @Autowired
    public OrderService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    public List<Order> findAll() {

        return orderRepository.findAll();
    }

    public Optional<Order> findOrderById(UUID orderId) {

        return orderRepository.findById(orderId);
    }

    public List<Order> findOrdersByCustomer(UUID customerId) {

        return orderRepository.findAllByCustomerId(customerId);
    }

    public Order save(Order order) {

        return orderRepository.save(order);
    }
}
