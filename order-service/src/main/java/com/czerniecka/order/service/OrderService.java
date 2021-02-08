package com.czerniecka.order.service;

import com.czerniecka.order.dto.OrderDTO;
import com.czerniecka.order.dto.OrderMapper;
import com.czerniecka.order.entity.Order;
import com.czerniecka.order.exception.CustomException;
import com.czerniecka.order.repository.OrderRepository;
import com.czerniecka.order.vo.Inventory;
import com.czerniecka.order.vo.Product;
import com.czerniecka.order.vo.OrderWithProductResponseVO;
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

    public Optional<OrderWithProductResponseVO> getOrderWithProduct(UUID orderId) {
        OrderWithProductResponseVO vo = new OrderWithProductResponseVO();
        Optional<Order> o = orderRepository.findById(orderId);

        if (o.isPresent()) {
            Order order = o.get();
            Product product = productServiceClient.getProduct(order.getProductId());
            product.setId(order.getProductId());
            vo.setOrder(orderMapper.toOrderDTO(order));
            vo.setProduct(product);
            return Optional.of(vo);
        } else {
            return Optional.empty();
        }
    }

    public List<OrderWithProductResponseVO> getOrdersWithProductsForCustomer(UUID customerId) {
        List<Order> orders = orderRepository.findAllByCustomerId(customerId);
        List<OrderWithProductResponseVO> result = new ArrayList<>();

        for (Order o : orders) {
            Product product = productServiceClient.getProduct(o.getProductId());
            OrderWithProductResponseVO vo = new OrderWithProductResponseVO();
            product.setId(o.getProductId());
            vo.setOrder(orderMapper.toOrderDTO(o));
            vo.setProduct(product);
            result.add(vo);
        }
        return result;
    }

    public Optional<OrderDTO> save(OrderDTO orderDTO) {
        Order order = orderMapper.toOrder(orderDTO);
        Order saved = orderRepository.save(order);

        /*If inventory-service is not available, service client will invoke fallback method and
        will stop executing rest of POST order*/
        Inventory inventory = inventoryServiceClient.getInventory(saved.getProductId());
        /* When trying to order more items then in stock - throw error*/
        if(inventory.getQuantity() < orderDTO.getAmount()){
            orderRepository.delete(saved);
            throw new CustomException("Order of " + orderDTO.getAmount() + " products not placed. Only "
                    + inventory.getQuantity() + " products are available" , HttpStatus.BAD_REQUEST);
        }
        /* When enough product in stock - update inventory -> remove items from stock */
        else {
            inventory.setQuantity(inventory.getQuantity() - saved.getAmount());
            HttpStatus httpStatus = inventoryServiceClient.putInventory(inventory);
            if (httpStatus.equals(HttpStatus.CREATED)) {
                return Optional.of(orderMapper.toOrderDTO(saved));
            } else {
                orderRepository.delete(saved);
                return Optional.empty();
            }
        }
    }

}
