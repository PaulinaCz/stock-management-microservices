package com.czerniecka.inventory.controller;

import com.czerniecka.inventory.dto.InventoryDTO;
import com.czerniecka.inventory.service.InventoryService;
import com.czerniecka.inventory.vo.InventoryProductResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/inventory")
public class InventoryController {

    private final InventoryService inventoryService;

    @Autowired
    public InventoryController(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }

    @GetMapping("")
    public ResponseEntity<List<InventoryDTO>> getAllInventory(){
        List<InventoryDTO> all = inventoryService.findAll();
        return ResponseEntity.ok(all);
    }

    @GetMapping("/products")
    public ResponseEntity<List<InventoryProductResponse>> getAllWithProducts(){
        List<InventoryProductResponse> allWithProducts = inventoryService.findAllWithProducts();
        return ResponseEntity.ok(allWithProducts);
    }

    @GetMapping("/{inventoryId}")
    public ResponseEntity<InventoryProductResponse> getInventoryById(@PathVariable UUID inventoryId){
        Optional<InventoryProductResponse> inventoryById = inventoryService.findInventoryById(inventoryId);
        return inventoryById.map(inventoryProductResponse -> new ResponseEntity<>(inventoryProductResponse, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @GetMapping("/product/{productId}")
    public ResponseEntity<InventoryDTO> getInventoryByProductId(@PathVariable UUID productId){
        Optional<InventoryDTO> i = inventoryService.findInventoryByProductId(productId);
        return i.map(inventoryDTO -> new ResponseEntity<>(inventoryDTO, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PostMapping("")
    public ResponseEntity<InventoryDTO> addInventory(@RequestBody InventoryDTO inventoryDTO){
        InventoryDTO saved = inventoryService.save(inventoryDTO);
        return new ResponseEntity<>(saved, HttpStatus.CREATED);
    }

    @PutMapping("/{inventoryId}")
    public ResponseEntity<Void> updateInventory(@PathVariable UUID inventoryId,
                                @RequestBody InventoryDTO inventoryDTO){

        if(!inventoryService.updateInventory(inventoryId, inventoryDTO)){
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }else{
            return new ResponseEntity<>(HttpStatus.CREATED);
        }
    }

}
