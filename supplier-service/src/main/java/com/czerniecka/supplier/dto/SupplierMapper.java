package com.czerniecka.supplier.dto;

import com.czerniecka.supplier.entity.Supplier;
import org.mapstruct.Mapper;


@Mapper(componentModel = "spring")
public interface SupplierMapper {

    SupplierDTO toSupplierDTO(Supplier supplier);
    Supplier toSupplier(SupplierDTO supplierDTO);
}
