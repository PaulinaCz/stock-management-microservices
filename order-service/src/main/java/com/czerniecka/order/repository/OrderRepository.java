package com.czerniecka.order.repository;

import com.czerniecka.order.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;
import java.util.UUID;

@Repository
public interface OrderRepository extends JpaRepository<Order, UUID> {

    List<Order> findAllByCustomerId(UUID customerId);

    @Transactional
    @Modifying
    @Query(value = "UPDATE Order o SET o.orderStatus = ?2 WHERE o.id = ?1")
    void changeOrderStatus(UUID orderId, String orderStatus);
}
