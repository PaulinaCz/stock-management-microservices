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

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

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

    public List<OrderDTO> findAll() {

        List<Order> all = orderRepository.findAll();
        return orderMapper.toOrdersDTOs(all);
    }

    /* If product-service is unavailable, returns Order with empty Product object*/
    public Optional<OrderProductResponse> getOrderWithProduct(UUID orderId) {
        OrderProductResponse response = new OrderProductResponse();
        Optional<Order> o = orderRepository.findById(orderId);

        if (o.isPresent()) {
            Order order = o.get();
            Product product = productServiceClient.getProduct(order.getProductId());
            product.setId(order.getProductId());
            response.setOrder(orderMapper.toOrderDTO(order));
            response.setProduct(product);
            return Optional.of(response);
        } else {
            return Optional.empty();
        }
    }

    /* If product-service is unavailable, returns List of Orders with empty Product objects */
    public List<OrderProductResponse> getOrdersWithProductsForCustomer(UUID customerId) {
        List<Order> orders = orderRepository.findAllByCustomerId(customerId);
        List<OrderProductResponse> result = new ArrayList<>();

        for (Order o : orders) {
            Product product = productServiceClient.getProduct(o.getProductId());
            OrderProductResponse response = new OrderProductResponse();
            product.setId(o.getProductId());
            response.setOrder(orderMapper.toOrderDTO(o));
            response.setProduct(product);
            result.add(response);
        }
        return result;
    }

    public Optional<OrderDTO> save(OrderDTO orderDTO) {
        Order order = orderMapper.toOrder(orderDTO);
        Order saved = orderRepository.save(order);

        /*If inventory-service is not available, service client will invoke fallback method and
        will stop executing rest of POST order*/
        Inventory inventory = inventoryServiceClient.getInventory(saved.getProductId());
        /* When enough items in stock - process order. If not - throw exception */
        if (inventory.getQuantity() > orderDTO.getAmount()) {
            inventory.setQuantity(inventory.getQuantity() - saved.getAmount());
            HttpStatus httpStatus = inventoryServiceClient.putInventory(inventory);

            if (httpStatus.equals(HttpStatus.CREATED)) {
                return Optional.of(orderMapper.toOrderDTO(saved));
            } else {
                orderRepository.delete(saved);
                return Optional.empty();
            }
        } else {
            orderRepository.delete(saved);
            throw new CustomException("Sorry. Unable to checkout - not enough items in stock.", HttpStatus.BAD_REQUEST);
        }

    }
}


