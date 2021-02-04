package com.czerniecka.supplier.controller;

import com.czerniecka.supplier.dto.SupplierDTO;
import com.czerniecka.supplier.service.SupplierService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<List<SupplierDTO>> getAllSuppliers(){
        List<SupplierDTO> all = supplierService.findAll();
        return ResponseEntity.ok(all);
    }

    @GetMapping("/{supplierId}")
    public ResponseEntity getSupplierById(@PathVariable UUID supplierId){
        Optional<SupplierDTO> supplier = supplierService.findSupplierById(supplierId);

        return supplier.map(supplierDTO -> new ResponseEntity(supplierDTO, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity(HttpStatus.NOT_FOUND));
    }

    @PostMapping("")
    public ResponseEntity<SupplierDTO> addSupplier(@RequestBody SupplierDTO supplierDTO){
        SupplierDTO saved = supplierService.save(supplierDTO);
        return new ResponseEntity<>(saved, HttpStatus.CREATED);
    }

    @PutMapping("/{supplierId}")
    public ResponseEntity<Void> updateSupplier(@PathVariable UUID supplierId,
                               @RequestBody SupplierDTO supplierDTO){

        if(!supplierService.updateSupplier(supplierId, supplierDTO)){
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }else{
            return new ResponseEntity<>(HttpStatus.CREATED);
        }
    }
}
