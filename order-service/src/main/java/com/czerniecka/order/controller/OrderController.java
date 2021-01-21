package com.czerniecka.order.controller;

import com.czerniecka.order.dto.OrderDTO;
import com.czerniecka.order.service.OrderService;
import com.czerniecka.order.vo.ResponseTemplateVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
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
    public List<OrderDTO> getAllOrders(){
        return orderService.findAll();
    }

//
//    @GetMapping("/{orderId}")
//    public Order getOrderById(@PathVariable UUID orderId){
//        Optional<Order> orderById = orderService.findOrderById(orderId);
//        return orderById.orElse(null);
//    }

    @GetMapping("/{orderId}")
    public ResponseTemplateVO getOrderWithProduct(@PathVariable UUID orderId){
        return orderService.getOrderWithProduct(orderId);
    }

    @GetMapping("/forCustomer/{customerId}")
    public List<ResponseTemplateVO> getOrdersWithProductsForCustomer(@PathVariable UUID customerId){
        return orderService.getOrdersWithProductsForCustomer(customerId);
    }

    @GetMapping("/customer/{customerId}")
    public List<OrderDTO> getAllByCustomerId(@PathVariable UUID customerId){
        return orderService.findAllByCustomerId(customerId);
    }

    @PostMapping("")
    public OrderDTO addOrder(@RequestBody OrderDTO orderDTO){
        return orderService.save(orderDTO);
    }
}
