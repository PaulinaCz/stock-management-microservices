package com.czerniecka.order.controller;

import com.czerniecka.order.dto.OrderDTO;
import com.czerniecka.order.service.OrderService;
import com.czerniecka.order.vo.OrderProductResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<List<OrderDTO>> getAllOrders(){
        List<OrderDTO> orders = orderService.findAll();
        return ResponseEntity.ok(orders);

    }

    @GetMapping("/{orderId}")
    public ResponseEntity<OrderProductResponse> getOrderWithProduct(@PathVariable UUID orderId){
        Optional<OrderProductResponse> orderWithProduct = orderService.getOrderWithProduct(orderId);

        return orderWithProduct
                .map(orderProductResponse -> new ResponseEntity<>(orderProductResponse, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @GetMapping("/customer/{customerId}")
    public ResponseEntity<List<OrderProductResponse>> getOrdersWithProductsForCustomer(@PathVariable UUID customerId){
        List<OrderProductResponse> orders = orderService.getOrdersWithProductsForCustomer(customerId);
        return ResponseEntity.ok(orders);
    }

    @PostMapping("")
    public ResponseEntity<OrderDTO> addOrder(@RequestBody OrderDTO orderDTO){
        Optional<OrderDTO> saved = orderService.save(orderDTO);
        return saved.map(order -> new ResponseEntity<>(order, HttpStatus.CREATED))
                .orElseGet(() -> new ResponseEntity("Service is currently busy. Please try again later.",
                        HttpStatus.SERVICE_UNAVAILABLE));
    }
}
