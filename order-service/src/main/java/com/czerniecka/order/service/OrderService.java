package com.czerniecka.order.service;

import com.czerniecka.order.dto.OrderDTO;
import com.czerniecka.order.dto.OrderMapper;
import com.czerniecka.order.entity.Order;
import com.czerniecka.order.repository.OrderRepository;
import com.czerniecka.order.vo.Product;
import com.czerniecka.order.vo.ResponseTemplateVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;
    private RestTemplate restTemplate;

    @Autowired
    public OrderService(OrderRepository orderRepository, OrderMapper orderMapper, RestTemplate restTemplate) {
        this.orderRepository = orderRepository;
        this.orderMapper = orderMapper;
        this.restTemplate = restTemplate;
    }

    public List<OrderDTO> findAll() {

        List<Order> all = orderRepository.findAll();
        return orderMapper.toOrdersDTOs(all);
    }

//    public Optional<Order> findOrderById(UUID orderId) {
//
//        return orderRepository.findById(orderId);
//    }

    public OrderDTO save(OrderDTO orderDTO) {

        Order order = orderMapper.toOrder(orderDTO);
        Order saved = orderRepository.save(order);
        return orderMapper.toOrderDTO(saved);
    }

    public ResponseTemplateVO getOrderWithProduct(UUID orderId) {
        ResponseTemplateVO vo = new ResponseTemplateVO();
        Order order = orderRepository.findOrderById(orderId);

        Product product = restTemplate.getForObject("http://localhost:3001/products/" + order.getProductId(),
                Product.class);

        product.setId(order.getProductId());
        vo.setOrderDTO(orderMapper.toOrderDTO(order));
        vo.setProduct(product);

        return vo;
    }

    public List<ResponseTemplateVO> getOrdersWithProductsForCustomer(UUID customerId) {
        List<Order> orders = orderRepository.findAllByCustomerId(customerId);
        List<ResponseTemplateVO> result = new ArrayList<>();

        for (Order o : orders
             ) {
            Product product =  restTemplate.getForObject("http://localhost:3001/products/" + o.getProductId(),
                    Product.class);
            ResponseTemplateVO vo = new ResponseTemplateVO();
            product.setId(o.getProductId());
            vo.setOrderDTO(orderMapper.toOrderDTO(o));
            vo.setProduct(product);
            result.add(vo);
        }
        return result;
    }

    public List<OrderDTO> findAllByCustomerId(UUID customerId) {

        List<Order> allByCustomerId = orderRepository.findAllByCustomerId(customerId);

        return orderMapper.toOrdersDTOs(allByCustomerId);
    }
}
