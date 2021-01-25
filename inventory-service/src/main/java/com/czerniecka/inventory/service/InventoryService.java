package com.czerniecka.inventory.service;

import com.czerniecka.inventory.InventoryRepository;
import com.czerniecka.inventory.dto.InventoryDTO;
import com.czerniecka.inventory.dto.InventoryMapper;
import com.czerniecka.inventory.entity.Inventory;
import com.czerniecka.inventory.vo.Product;
import com.czerniecka.inventory.vo.ResponseTemplateVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class InventoryService {

    private final InventoryRepository inventoryRepository;
    private final InventoryMapper inventoryMapper;
    private RestTemplate restTemplate;

    @Autowired
    public InventoryService(InventoryRepository inventoryRepository, InventoryMapper inventoryMapper, RestTemplate restTemplate) {
        this.inventoryRepository = inventoryRepository;
        this.inventoryMapper = inventoryMapper;
        this.restTemplate = restTemplate;
    }

    public List<InventoryDTO> findAll() {

        List<Inventory> all = inventoryRepository.findAll();
        return inventoryMapper.toInventoryDTOs(all);
    }

    public List<ResponseTemplateVO> getInventoryWithProducts() {
        List<Inventory> inventories = inventoryRepository.findAll();
        List<ResponseTemplateVO> result = new ArrayList<>();

        for(Inventory i : inventories){
            Product product = restTemplate.getForObject("http://localhost:3001/products/" + i.getProductId(),
                                                        Product.class);
            ResponseTemplateVO vo = new ResponseTemplateVO();
            vo.setInventoryDTO(inventoryMapper.toInventoryDTO(i));
            vo.setProduct(product);
            result.add(vo);
        }
        return result;
    }

    public Optional<ResponseTemplateVO> findInventoryById(UUID inventoryId) {
        Optional<Inventory> i = inventoryRepository.findById(inventoryId);
        ResponseTemplateVO vo = new ResponseTemplateVO();

        if(i.isPresent()){
            Inventory inventory = i.get();
            Product product = restTemplate.getForObject("http://localhost:3001/products/" + inventory.getProductId(),
                    Product.class);
            vo.setInventoryDTO(inventoryMapper.toInventoryDTO(inventory));
            vo.setProduct(product);
            return Optional.of(vo);
        }else{
            return Optional.empty();
        }
    }

    public InventoryDTO save(InventoryDTO inventoryDTO) {
        Inventory inventory = inventoryMapper.toInventory(inventoryDTO);
        Inventory saved = inventoryRepository.save(inventory);
        return inventoryMapper.toInventoryDTO(saved);
    }

    public boolean updateInventory(UUID inventoryId, InventoryDTO inventoryDTO) {
        Optional<Inventory> i = inventoryRepository.findById(inventoryId);

        if(i.isPresent()){
            Inventory inventory = i.get();
            inventory.setLastModified(LocalDateTime.now());
            inventory.setProductId(inventoryDTO.getProductId());
            inventory.setQuantity(inventoryDTO.getQuantity());

            inventoryRepository.save(inventory);
            return true;
        }else{
            return false;
        }
    }


}
