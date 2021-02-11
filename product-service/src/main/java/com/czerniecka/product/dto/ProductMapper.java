package com.czerniecka.product.dto;

import com.czerniecka.product.entity.Product;
import org.mapstruct.Mapper;
import reactor.core.publisher.Flux;

@Mapper(componentModel = "spring")
public interface ProductMapper {

    ProductDTO toProductDTO(Product product);
    ProductDTO toProductsDTOs(Flux<Product> products);
    Product toProduct(ProductDTO productDTO);
}
