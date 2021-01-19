package com.czerniecka.supplier.service;

import com.czerniecka.supplier.entity.Supplier;
import com.czerniecka.supplier.repository.SupplierRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class SupplierService {

    private final SupplierRepository supplierRepository;

    @Autowired
    public SupplierService(SupplierRepository supplierRepository) {
        this.supplierRepository = supplierRepository;
    }

    public List<Supplier> findAll() {

        return supplierRepository.findAll();
    }

    public Optional<Supplier> findById(UUID supplierId) {

        return supplierRepository.findById(supplierId);
    }

    public Supplier save(Supplier supplier) {

        return supplierRepository.save(supplier);
    }
}
