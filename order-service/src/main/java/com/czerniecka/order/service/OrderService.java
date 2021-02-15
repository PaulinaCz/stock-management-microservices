package com.czerniecka.order.service;

import com.czerniecka.order.client.InventoryServiceClient;
import com.czerniecka.order.client.ProductServiceClient;
import com.czerniecka.order.dto.OrderDTO;
import com.czerniecka.order.dto.OrderMapper;
import com.czerniecka.order.entity.Order;
import com.czerniecka.order.repository.OrderRepository;
import com.czerniecka.order.vo.Inventory;
import com.czerniecka.order.vo.Product;
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
     *  If product-service is not available method returns:
     *  Order with empty Product object
     *  
     *  If order is not found returns Mono.empty
     */
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
    /**
     * If inventory-service is not available while calling on GET method - fallback method will return empty Inventory object.
     * If save method receives empty Invoice object, it reverses save order to database.
     *
     * If number of available items in inventory is larger than the order amount -> proceed to update inventory WHERE:
     * If inventory-service is not available while calling on PUT method 
     * it will invoke fallback method and return empty Inventory object and reverse save order.
     * If PUT method on inventory-service is successful - both database changes are committed.
     * 
     * If number of available items in inventory is less than the order amount -> 
     * does not proceed to update inventory & reverse save order to database.
    */

    public Mono<OrderDTO> save(OrderDTO orderDTO) {

        Order order = orderMapper.toOrder(orderDTO);
        Mono<Order> saved = orderRepository.save(order);
        
        return saved.flatMap(
                o -> {
                    Inventory inventory = inventoryServiceClient.getInventory(o.getProductId());
                    if(inventory.getId() == null){
                        orderRepository.delete(o);
                        return Mono.empty();
                    }else if(inventory.getQuantity() > o.getAmount()){
                        inventory.setQuantity(inventory.getQuantity() - o.getAmount());
                        Inventory i = inventoryServiceClient.putInventory(inventory);
                        if(i.getId() == null){
                            orderRepository.delete(o);
                            return Mono.empty();
                        }else{
                            return Mono.just(orderMapper.toOrderDTO(o));
                        }
                    }else{
                        orderRepository.delete(o);
                        return Mono.empty();
                    }
                });
        
    }
//
//    public void updateOrderStatus(String orderId, String orderStatus) {
//        orderRepository.changeOrderStatus(orderId, orderStatus);
//    }

}


