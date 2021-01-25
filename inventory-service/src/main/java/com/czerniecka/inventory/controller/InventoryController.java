package com.czerniecka.inventory.controller;

import com.czerniecka.inventory.dto.InventoryDTO;
import com.czerniecka.inventory.service.InventoryService;
import com.czerniecka.inventory.vo.ResponseTemplateVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
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

    @GetMapping("/withProducts")
    public ResponseEntity<List<ResponseTemplateVO>> getInventoryWithProducts(){
        List<ResponseTemplateVO> allWithProducts = inventoryService.getInventoryWithProducts();
        return ResponseEntity.ok(allWithProducts);
    }

    @PostMapping("")
    public ResponseEntity<InventoryDTO> addInventory(@RequestBody InventoryDTO inventoryDTO){
        InventoryDTO saved = inventoryService.save(inventoryDTO);
        return new ResponseEntity<>(saved, HttpStatus.CREATED);
    }

    @PutMapping("/inventory/{inventoryId}")
    public ResponseEntity<Void> updateInventory(@PathVariable UUID inventoryId,
                                @RequestBody InventoryDTO inventoryDTO){

        if(!inventoryService.updateInventory(inventoryId, inventoryDTO)){
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }else{
            return new ResponseEntity<>(HttpStatus.CREATED);
        }
    }
}
