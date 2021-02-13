package com.czerniecka.order.service;

import com.czerniecka.order.client.InventoryServiceClient;
import com.czerniecka.order.client.ProductServiceClient;
import com.czerniecka.order.dto.OrderDTO;
import com.czerniecka.order.dto.OrderMapper;
import com.czerniecka.order.entity.Order;
import com.czerniecka.order.exception.CustomException;
import com.czerniecka.order.repository.OrderRepository;
import com.czerniecka.order.vo.Inventory;
import com.czerniecka.order.vo.Product;
import com.czerniecka.order.vo.OrderProductResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;


@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;
    private final ProductServiceClient productServiceClient;
    private final InventoryServiceClient inventoryServiceClient;

    @Autowired
    public OrderService(OrderRepository orderRepository, OrderMapper orderMapper, ProductServiceClient productServiceClient, InventoryServiceClient inventoryServiceClient) {
        this.orderRepository = orderRepository;
        this.orderMapper = orderMapper;
        this.productServiceClient = productServiceClient;
        this.inventoryServiceClient = inventoryServiceClient;
    }

    public Flux<OrderDTO> findAll() {

        Flux<Order> all = orderRepository.findAll();
        return all.map(orderMapper::toOrderDTO);
    }

    /* If product-service is unavailable, returns Order with empty Product object*/
    public Mono<OrderProductResponse> getOrderWithProduct(String orderId) {
        OrderProductResponse response = new OrderProductResponse();
        Mono<Order> order = orderRepository.findById(orderId);
        
        return order.flatMap(
                o -> {
                    Product product = productServiceClient.getProduct(o.getProductId());
                    product.setId(o.getProductId());
                    response.setOrder(orderMapper.toOrderDTO(o));
                    response.setProduct(product);
                    return Mono.just(response);
                }
        ).or(Mono.empty());

    }


    public Inventory getInventory(String productId) {
        return inventoryServiceClient.getInventory(productId);
    }

    /*
    If inventory-service is not available while calling on GET method - fallback method will return empty Inventory object.
    If save method receives empty Invoice object, it reverses save order to database.

    If inventory-service is not available while calling on PUT method it will invoke fallback method.
    If save method receives response HttpStatus.CREATED from inventory-service, it returns saved order.
    If save method receives response on failure to connect to/save to inventory-service, it reverses save order to database.
    */
    @Transactional(rollbackFor = CustomException.class)
    public Mono<OrderDTO> save(OrderDTO orderDTO) {

        Order order = orderMapper.toOrder(orderDTO);
        Mono<Order> saved = orderRepository.save(order);
        
        return saved.flatMap(
                o -> {
                    Inventory inventory = getInventory(o.getProductId());
                  
                    inventory.setQuantity(inventory.getQuantity() - o.getAmount());
                    HttpStatus status = inventoryServiceClient.putInventory(inventory);
                    if(status.equals(HttpStatus.CREATED)){
                        return Mono.just(orderMapper.toOrderDTO(o));
                    }else{
                        return Mono.empty();
                    }
                }
        ).or(Mono.empty());
        
    }
//
//    public void updateOrderStatus(String orderId, String orderStatus) {
//        orderRepository.changeOrderStatus(orderId, orderStatus);
//    }

}


