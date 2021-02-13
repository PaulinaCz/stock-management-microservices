package com.czerniecka.supplier.service;

import com.czerniecka.supplier.dto.SupplierDTO;
import com.czerniecka.supplier.dto.SupplierMapper;
import com.czerniecka.supplier.entity.Supplier;
import com.czerniecka.supplier.repository.SupplierRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;


@Service
public class SupplierService {

    private final SupplierRepository supplierRepository;
    private final SupplierMapper supplierMapper;

    @Autowired
    public SupplierService(SupplierRepository supplierRepository, SupplierMapper supplierMapper) {
        this.supplierRepository = supplierRepository;
        this.supplierMapper = supplierMapper;
    }

    public Flux<SupplierDTO> findAll() {

        Flux<Supplier> all = supplierRepository.findAll();
        return all.map(supplierMapper::toSupplierDTO);
    }

    public Mono<SupplierDTO> findSupplierById(String supplierId) {

        Mono<Supplier> byId = supplierRepository.findById(supplierId);
        return byId.map(supplierMapper::toSupplierDTO);
    }

    public Mono<SupplierDTO> save(SupplierDTO supplierDTO) {

        Supplier supplier = supplierMapper.toSupplier(supplierDTO);
        Mono<Supplier> saved = supplierRepository.save(supplier);
        return saved.map(supplierMapper::toSupplierDTO);
    }

    public Mono<SupplierDTO> updateSupplier(String supplierId, SupplierDTO supplierDTO) {

        return supplierRepository.findById(supplierId)
                .switchIfEmpty(Mono.empty())
                .flatMap(supplier -> {
                    supplier.setName(supplierDTO.getName());
                    supplier.setEmail(supplierDTO.getEmail());
                    Mono<Supplier> updated = supplierRepository.save(supplier);
                    return updated.map(supplierMapper::toSupplierDTO);
                });

    }
}
