package com.czerniecka.supplier.dto;

import com.czerniecka.supplier.entity.Supplier;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface SupplierMapper {

    SupplierDTO toSupplierDTO(Supplier supplier);
    List<SupplierDTO> toSuppliersDTOs(List<Supplier> suppliers);
    Supplier toSupplier(SupplierDTO supplierDTO);
}
