package com.czerniecka.product.dto;

import com.czerniecka.product.entity.Product;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ProductMapper {

    ProductDTO toProductDTO(Product product);
    List<ProductDTO> toProductsDTOs(List<Product> products);
    Product toProduct(ProductDTO productDTO);
}
