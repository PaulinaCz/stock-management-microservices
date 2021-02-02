package com.czerniecka.order.controller;

import com.czerniecka.order.dto.OrderDTO;
import com.czerniecka.order.service.OrderService;
import com.czerniecka.order.vo.ResponseTemplateVO;
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
    public ResponseEntity<ResponseTemplateVO> getOrderWithProduct(@PathVariable UUID orderId){
        Optional<ResponseTemplateVO> orderWithProduct = orderService.getOrderWithProduct(orderId);

        return orderWithProduct.map(responseTemplateVO -> new ResponseEntity<>(responseTemplateVO, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @GetMapping("/customer/{customerId}")
    public ResponseEntity<List<ResponseTemplateVO>> getOrdersWithProductsForCustomer(@PathVariable UUID customerId){
        List<ResponseTemplateVO> orders = orderService.getOrdersWithProductsForCustomer(customerId);
        return ResponseEntity.ok(orders);
    }

    @PostMapping("")
    public ResponseEntity<OrderDTO> addOrder(@RequestBody OrderDTO orderDTO){
        OrderDTO saved = orderService.save(orderDTO);
        return new ResponseEntity<>(saved, HttpStatus.CREATED);
    }
}
