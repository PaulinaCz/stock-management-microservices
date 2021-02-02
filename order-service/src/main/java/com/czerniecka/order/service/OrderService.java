package com.czerniecka.order.service;

import com.czerniecka.order.dto.OrderDTO;
import com.czerniecka.order.dto.OrderMapper;
import com.czerniecka.order.entity.Order;
import com.czerniecka.order.repository.OrderRepository;
import com.czerniecka.order.vo.Inventory;
import com.czerniecka.order.vo.Product;
import com.czerniecka.order.vo.ResponseTemplateVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;
    private RestTemplate restTemplate;
    private final ProductServiceClient productServiceClient;

    @Autowired
    public OrderService(OrderRepository orderRepository, OrderMapper orderMapper, RestTemplate restTemplate, ProductServiceClient productServiceClient) {
        this.orderRepository = orderRepository;
        this.orderMapper = orderMapper;
        this.restTemplate = restTemplate;
        this.productServiceClient = productServiceClient;
    }

    public List<OrderDTO> findAll() {

        List<Order> all = orderRepository.findAll();
        return orderMapper.toOrdersDTOs(all);
    }

    public Optional<ResponseTemplateVO> getOrderWithProduct(UUID orderId) {
        ResponseTemplateVO vo = new ResponseTemplateVO();
        Optional<Order> o = orderRepository.findById(orderId);

        if(o.isPresent()){
            Order order = o.get();
            Product product = productServiceClient.getProduct(order.getProductId());
            product.setId(order.getProductId());
            vo.setOrder(orderMapper.toOrderDTO(order));
            vo.setProduct(product);
            return Optional.of(vo);
        }else{
            return Optional.empty();
        }
    }

    public List<ResponseTemplateVO> getOrdersWithProductsForCustomer(UUID customerId) {
        List<Order> orders = orderRepository.findAllByCustomerId(customerId);
        List<ResponseTemplateVO> result = new ArrayList<>();

        for (Order o : orders
             ) {
            Product product =  productServiceClient.getProduct(o.getProductId());
            ResponseTemplateVO vo = new ResponseTemplateVO();
            product.setId(o.getProductId());
            vo.setOrder(orderMapper.toOrderDTO(o));
            vo.setProduct(product);
            result.add(vo);
        }
        return result;
    }

    public OrderDTO save(OrderDTO orderDTO) {
        Order order = orderMapper.toOrder(orderDTO);
        // update inventory -> remove items from stock

        Inventory inventory = restTemplate.getForObject("http://inventory-service/inventory/product/" + order.getProductId()
                ,Inventory.class);
        inventory.setQuantity(inventory.getQuantity() - order.getAmount());
        restTemplate.put("http://inventory-service/inventory/inventory/" + inventory.getId(), inventory, Inventory.class);
        Order saved = orderRepository.save(order);

        return orderMapper.toOrderDTO(saved);
    }

}
