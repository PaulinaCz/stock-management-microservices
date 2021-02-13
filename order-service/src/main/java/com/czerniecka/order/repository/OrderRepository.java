package com.czerniecka.order.repository;

import com.czerniecka.order.entity.Order;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

import java.util.UUID;

@Repository
public interface OrderRepository extends ReactiveCrudRepository<Order, String> {

//    @Query(value = "UPDATE Order o SET o.orderStatus = ?2 WHERE o.id = ?1")
//    void changeOrderStatus(UUID String, String orderStatus);
}
