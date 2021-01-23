package com.czerniecka.supplier.controller;

import com.czerniecka.supplier.dto.SupplierDTO;
import com.czerniecka.supplier.service.SupplierService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/suppliers")
public class SupplierController {

    private final SupplierService supplierService;

    @Autowired
    public SupplierController(SupplierService supplierService) {
        this.supplierService = supplierService;
    }

    @GetMapping("")
    public List<SupplierDTO> getAllSuppliers(){
        return supplierService.findAll();
    }

    @GetMapping("{supplierId}")
    public SupplierDTO getSupplierById(@PathVariable UUID supplierId){
        return supplierService.findById(supplierId);
    }

    @PostMapping("")
    public SupplierDTO addSupplier(@RequestBody SupplierDTO supplierDTO){
        return supplierService.save(supplierDTO);
    }

    @PutMapping("/supplier/{supplierId}")
    public void updateSupplier(@PathVariable UUID supplierId,
                               @RequestBody SupplierDTO supplierDTO){

        supplierService.updateSupplier(supplierId, supplierDTO);
    }
}
