package com.czerniecka.supplier.service;

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

    public Optional<SupplierDTO> findSupplierById(UUID supplierId) {

        Optional<Supplier> byId = supplierRepository.findById(supplierId);
        return byId.map(supplierMapper::toSupplierDTO);
    }

    public SupplierDTO save(SupplierDTO supplierDTO) {

        Supplier supplier = supplierMapper.toSupplier(supplierDTO);
        Supplier saved = supplierRepository.save(supplier);
        return supplierMapper.toSupplierDTO(saved);
    }

    public boolean updateSupplier(UUID supplierId, SupplierDTO supplierDTO) {

        Optional<Supplier> s = supplierRepository.findById(supplierId);

        if(s.isPresent()){
            Supplier supplier = s.get();
            supplier.setName(supplierDTO.getName());
            supplier.setEmail(supplierDTO.getEmail());

            supplierRepository.save(supplier);
            return true;
        }else{
            return false;
        }
    }
}
