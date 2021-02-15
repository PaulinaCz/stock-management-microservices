package com.czerniecka.inventory.dto;

import com.czerniecka.inventory.entity.Inventory;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface InventoryMapper {

    InventoryDTO toInventoryDTO(Inventory inventory);
    Inventory toInventory(InventoryDTO inventoryDTO);
}
