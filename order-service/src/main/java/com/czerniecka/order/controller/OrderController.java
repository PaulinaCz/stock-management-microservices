package com.czerniecka.order.controller;

import com.czerniecka.order.dto.OrderDTO;
import com.czerniecka.order.exceptions.ItemNotAvailable;
import com.czerniecka.order.exceptions.OrderNotFound;
import com.czerniecka.order.service.OrderService;
import com.czerniecka.order.vo.OrderProductResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.naming.ServiceUnavailableException;
import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/orders")
public class OrderController {

    private final OrderService orderService;

    @Autowired
    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping("")
    public Flux<OrderDTO> getAllOrders(){
        return orderService.findAll();
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/{id}")
    public Mono<OrderProductResponse> getOrderWithProduct(@PathVariable("id") String orderId){
        
        return orderService.getOrderWithProduct(orderId)
                .switchIfEmpty(Mono.error(new OrderNotFound(orderId)));
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("")
    public Mono<OrderDTO> addOrder(@Valid @RequestBody OrderDTO orderDTO){
        
        return orderService.save(orderDTO);
    }
//
//    @PatchMapping("/{orderId}")
//    public ResponseEntity updateOrderStatus(@PathVariable String orderId, @RequestBody @NotNull String orderStatus){
//        orderService.updateOrderStatus(orderId, orderStatus);
//        return ResponseEntity.ok("Order status updated");
//    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(OrderNotFound.class)
    public Map<String, Object> handleNotFound(OrderNotFound e){

        Map<String, Object> errorBody = new HashMap<>();

        errorBody.put("timestamp", LocalDateTime.now());
        errorBody.put("error", "Order " + e.getMessage() + " not found");

        return errorBody;
    }

    @ResponseStatus(HttpStatus.SERVICE_UNAVAILABLE)
    @ExceptionHandler(ServiceUnavailableException.class)
    public Map<String, Object> handleNotSaved(ServiceUnavailableException e){

        Map<String, Object> errorBody = new HashMap<>();

        errorBody.put("timestamp", LocalDateTime.now());
        errorBody.put("error", e.getMessage());

        return errorBody;
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(ItemNotAvailable.class)
    public Map<String, Object> handleNotInStock(ItemNotAvailable e){

        Map<String, Object> errorBody = new HashMap<>();

        errorBody.put("timestamp", LocalDateTime.now());
        errorBody.put("error", e.getMessage());

        return errorBody;
    }

    //TODO: fix input validation & exception handler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Map<String, Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex) {

        Map<String, Object> errorBody = new HashMap<>();

        errorBody.put("timestamp", LocalDateTime.now());

        List<String> errors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .collect(Collectors.toList());

        errorBody.put("validationErrors", errors);
        return errorBody;
    }
}
