package com.czerniecka.order.controller;

import com.czerniecka.order.entity.Order;
import com.czerniecka.order.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/orders")
public class OrderController {

    private final OrderService orderService;

    @Autowired
    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping("")
    public List<Order> getAllOrders(){
        return orderService.findAll();
    }

    @GetMapping("/{orderId}")
    public Order getOrderById(@PathVariable UUID orderId){
        Optional<Order> orderById = orderService.findOrderById(orderId);
        return orderById.orElse(null);
    }

    @GetMapping("/customer/{customerId}")
    public List<Order> getOrderByCustomer(@PathVariable UUID customerId){
        return orderService.findOrdersByCustomer(customerId);
    }

    @PostMapping("")
    public Order addOrder(@RequestBody Order order){
        return orderService.save(order);
    }
}