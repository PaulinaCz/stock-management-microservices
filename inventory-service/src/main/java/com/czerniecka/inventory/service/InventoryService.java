package com.czerniecka.inventory.service;

import com.czerniecka.inventory.InventoryRepository;
import com.czerniecka.inventory.dto.InventoryDTO;
import com.czerniecka.inventory.dto.InventoryMapper;
import com.czerniecka.inventory.entity.Inventory;
import com.czerniecka.inventory.vo.Product;
import com.czerniecka.inventory.vo.ResponseTemplateVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

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
}
