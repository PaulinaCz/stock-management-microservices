package com.czerniecka.inventory.service;

import com.czerniecka.inventory.InventoryRepository;
import com.czerniecka.inventory.dto.InventoryDTO;
import com.czerniecka.inventory.dto.InventoryMapper;
import com.czerniecka.inventory.entity.Inventory;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mapstruct.factory.Mappers;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class InventoryServiceTest {

    @Mock
    InventoryRepository inventoryRepository;
    @Spy
    InventoryMapper inventoryMapper = Mappers.getMapper(InventoryMapper.class);
    @Mock
    RestTemplate restTemplate;

    InventoryService inventoryService;

    @Before
    public void init(){
        inventoryService = new InventoryService(inventoryRepository, inventoryMapper, restTemplate);
    }

    @Test
    public void shouldMapInventoryToInventoryDTO(){

        UUID inventoryId = UUID.randomUUID();
        UUID productId = UUID.randomUUID();
        Inventory inventory = new Inventory(inventoryId, productId, 10, LocalDateTime.now());

        InventoryDTO inventoryDTO = inventoryMapper.toInventoryDTO(inventory);

        assertThat(inventoryDTO.getProductId()).isEqualTo(productId);
        assertThat(inventoryDTO.getQuantity()).isEqualTo(10);

    }

    @Test
    public void shouldReturnEmptyList(){

        when(inventoryRepository.findAll()).thenReturn(List.of());
        List<InventoryDTO> all = inventoryService.findAll();

        assertEquals(0, all.size());

    }

    @Test
    public void shouldReturnSavedInventory(){

        UUID inventoryId = UUID.randomUUID();
        UUID productId = UUID.randomUUID();
        Inventory inventory = new Inventory(inventoryId, productId, 10, LocalDateTime.now());

        InventoryDTO inventoryDTO = new InventoryDTO();
        when(inventoryRepository.save(inventoryMapper.toInventory(inventoryDTO))).thenReturn(inventory);

        InventoryDTO saved = inventoryService.save(inventoryDTO);

        assertThat(saved).isNotNull();
        assertThat(saved.getQuantity()).isEqualTo(10);
        assertThat(saved.getId()).isEqualTo(inventoryId);
        assertThat(saved.getProductId()).isEqualTo(productId);
    }

}