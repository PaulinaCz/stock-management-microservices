package com.czerniecka.order.service;

import com.czerniecka.order.entity.Order;
import com.czerniecka.order.repository.OrderRepository;
import com.czerniecka.order.vo.Product;
import com.czerniecka.order.vo.ResponseTemplateVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.UUID;

@Service
public class OrderService {

    private final OrderRepository orderRepository;

    private RestTemplate restTemplate;

    @Autowired
    public OrderService(OrderRepository orderRepository, RestTemplate restTemplate) {
        this.orderRepository = orderRepository;
        this.restTemplate = restTemplate;
    }

    public List<Order> findAll() {

        return orderRepository.findAll();
    }

//    public Optional<Order> findOrderById(UUID orderId) {
//
//        return orderRepository.findById(orderId);
//    }

    public Order save(Order order) {

        return orderRepository.save(order);
    }

    public ResponseTemplateVO getOrderWithProduct(UUID orderId) {
        ResponseTemplateVO vo = new ResponseTemplateVO();
        Order order = orderRepository.findOrderById(orderId);

        Product product = restTemplate.getForObject("http://localhost:3001/products/" + order.getProductId(),
                                                    Product.class);

        vo.setOrder(order);
        vo.setProduct(product);

        return vo;
    }
}
