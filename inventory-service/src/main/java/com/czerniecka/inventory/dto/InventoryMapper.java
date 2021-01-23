package com.czerniecka.inventory.dto;

import com.czerniecka.inventory.entity.Inventory;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface InventoryMapper {

    InventoryDTO toInventoryDTO(Inventory inventory);
    List<InventoryDTO> toInventoryDTOs(List<Inventory> inventories);
    Inventory toInventory(InventoryDTO inventoryDTO);
}
