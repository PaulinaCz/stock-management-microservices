package com.czerniecka.inventory.controller;

import com.czerniecka.inventory.dto.InventoryDTO;
import com.czerniecka.inventory.entity.Inventory;
import com.czerniecka.inventory.service.InventoryService;
import com.czerniecka.inventory.vo.ResponseTemplateVO;
import org.springframework.beans.factory.annotation.Autowired;
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
    public List<InventoryDTO> getAllInventory(){
        return inventoryService.findAll();
    }

    @GetMapping("/withProducts")
    public List<ResponseTemplateVO> getInventoryWithProducts(){
        return inventoryService.getInventoryWithProducts();
    }

    @PostMapping("")
    public InventoryDTO addInventory(@RequestBody InventoryDTO inventoryDTO){
        return inventoryService.save(inventoryDTO);
    }

    @PutMapping("/inventory/{inventoryId}")
    public void updateInventory(@PathVariable UUID inventoryId,
                                @RequestBody InventoryDTO inventoryDTO){

        inventoryService.updateInventory(inventoryId, inventoryDTO);

    }
}
