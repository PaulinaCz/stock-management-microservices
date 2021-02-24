package com.czerniecka.product.dto;

import com.czerniecka.product.entity.Product;
import org.mapstruct.Mapper;


@Mapper(componentModel = "spring")
public interface ProductMapper {

    ProductDTO toProductDTO(Product product);
    Product toCreateProduct(CreateProductDTO productDTO);
}
