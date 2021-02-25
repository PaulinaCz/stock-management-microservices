package com.czerniecka.order.service;

import com.czerniecka.order.client.InventoryServiceClient;
import com.czerniecka.order.client.ProductServiceClient;
import com.czerniecka.order.dto.OrderDTO;
import com.czerniecka.order.dto.OrderMapper;
import com.czerniecka.order.entity.Order;
import com.czerniecka.order.repository.OrderRepository;
import com.czerniecka.order.vo.Inventory;
import com.czerniecka.order.vo.OrderProductResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
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

    /**
     * If product-service is not available method returns:
     * Mono of OrderProductResponse
     * with Order object and empty Product object
     * <p>
     * If order is not found returns Mono.empty
     */
    public Mono<OrderProductResponse> getOrderWithProduct(String orderId) {
        var order = orderRepository.findById(orderId);
        return order.switchIfEmpty(Mono.empty())
                .flatMap(o -> productServiceClient.getProduct(o.getProductId(), orderMapper.toOrderDTO(o)));
    }

    public Mono<OrderDTO> save(OrderDTO orderDTO) {

        Order order = orderMapper.toOrder(orderDTO);

        Mono<Inventory> inventoryMono = inventoryServiceClient.updateInventory(orderDTO);

        return inventoryMono.switchIfEmpty(Mono.empty())
                .flatMap(i -> orderRepository.save(order).map(orderMapper::toOrderDTO));

    }
//
//    public void updateOrderStatus(String orderId, String orderStatus) {
//        orderRepository.changeOrderStatus(orderId, orderStatus);
//    }

}


