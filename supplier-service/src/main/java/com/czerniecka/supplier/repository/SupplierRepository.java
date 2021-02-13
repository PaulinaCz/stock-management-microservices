package com.czerniecka.supplier.repository;

import com.czerniecka.supplier.entity.Supplier;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface SupplierRepository extends ReactiveCrudRepository<Supplier, String> {
}
