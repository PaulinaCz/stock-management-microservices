package com.czerniecka.customer.repository;

import com.czerniecka.customer.entity.Customer;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface CustomerRepository extends ReactiveCrudRepository<Customer, String> {
}
