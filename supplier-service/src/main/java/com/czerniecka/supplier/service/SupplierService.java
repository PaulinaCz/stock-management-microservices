package com.czerniecka.supplier.service;

import com.czerniecka.supplier.controller.NotFoundException;
import com.czerniecka.supplier.dto.SupplierDTO;
import com.czerniecka.supplier.dto.SupplierMapper;
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
    private final SupplierMapper supplierMapper;

    @Autowired
    public SupplierService(SupplierRepository supplierRepository, SupplierMapper supplierMapper) {
        this.supplierRepository = supplierRepository;
        this.supplierMapper = supplierMapper;
    }

    public List<SupplierDTO> findAll() {

        List<Supplier> all = supplierRepository.findAll();
        return supplierMapper.toSuppliersDTOs(all);
    }

    public SupplierDTO findById(UUID supplierId) {

        Optional<Supplier> byId = supplierRepository.findById(supplierId);

        return byId.map(supplierMapper::toSupplierDTO).orElse(null);
    }

    public SupplierDTO save(SupplierDTO supplierDTO) {

        Supplier supplier = supplierMapper.toSupplier(supplierDTO);
        Supplier saved = supplierRepository.save(supplier);
        return supplierMapper.toSupplierDTO(saved);
    }

    public void updateSupplier(UUID supplierId, SupplierDTO supplierDTO) {

        Supplier s = supplierRepository.findById(supplierId).orElseThrow(NotFoundException::new);

        s.setName(supplierDTO.getName());
        s.setEmail(supplierDTO.getEmail());

        supplierRepository.save(s);
    }
}
