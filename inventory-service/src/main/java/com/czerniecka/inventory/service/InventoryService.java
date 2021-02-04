package com.czerniecka.inventory.service;

import com.czerniecka.inventory.InventoryRepository;
import com.czerniecka.inventory.dto.InventoryDTO;
import com.czerniecka.inventory.dto.InventoryMapper;
import com.czerniecka.inventory.entity.Inventory;
import com.czerniecka.inventory.vo.Product;
import com.czerniecka.inventory.vo.ResponseTemplateVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class InventoryService {

    private final InventoryRepository inventoryRepository;
    private final InventoryMapper inventoryMapper;
    private final ProductServiceClient productServiceClient;

    @Autowired
    public InventoryService(InventoryRepository inventoryRepository, InventoryMapper inventoryMapper, ProductServiceClient productServiceClient) {
        this.inventoryRepository = inventoryRepository;
        this.inventoryMapper = inventoryMapper;
        this.productServiceClient = productServiceClient;
    }

    public List<InventoryDTO> findAll() {

        List<Inventory> all = inventoryRepository.findAll();
        return inventoryMapper.toInventoryDTOs(all);
    }

    public List<ResponseTemplateVO> findAllWithProducts() {
        List<Inventory> inventories = inventoryRepository.findAll();
        List<ResponseTemplateVO> result = new ArrayList<>();

        for (Inventory i : inventories) {
            Product product = productServiceClient.getProduct(i.getProductId());
            product.setId(i.getProductId());
            ResponseTemplateVO vo = new ResponseTemplateVO();
            vo.setInventory(inventoryMapper.toInventoryDTO(i));
            vo.setProduct(product);
            result.add(vo);
        }
        return result;
    }

    public Optional<ResponseTemplateVO> findInventoryById(UUID inventoryId) {
        Optional<Inventory> i = inventoryRepository.findById(inventoryId);
        ResponseTemplateVO vo = new ResponseTemplateVO();

        if (i.isPresent()) {
            Inventory inventory = i.get();
            Product product = productServiceClient.getProduct(inventory.getProductId());
            product.setId(inventory.getProductId());
            vo.setInventory(inventoryMapper.toInventoryDTO(inventory));
            vo.setProduct(product);
            return Optional.of(vo);
        } else {
            return Optional.empty();
        }
    }

    public Optional<InventoryDTO> findInventoryByProductId(UUID productId) {
        Optional<Inventory> inventory = inventoryRepository.findByProductId(productId);
        return inventory.map(inventoryMapper::toInventoryDTO);
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
