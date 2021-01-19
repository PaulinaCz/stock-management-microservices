package com.czerniecka.supplier.controller;

import com.czerniecka.supplier.entity.Supplier;
import com.czerniecka.supplier.service.SupplierService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
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
    public List<Supplier> getAllSuppliers(){
        return supplierService.findAll();
    }

    @GetMapping("{supplierId}")
    public Supplier getSupplierById(@PathVariable UUID supplierId){
        Optional<Supplier> supplierById = supplierService.findById(supplierId);
        return supplierById.orElse(null);

    }

    @PostMapping("")
    public Supplier addSupplier(@RequestBody Supplier supplier){
        return supplierService.save(supplier);
    }
}
