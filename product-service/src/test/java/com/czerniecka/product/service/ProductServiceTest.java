package com.czerniecka.product.service;

import com.czerniecka.product.dto.ProductDTO;
import com.czerniecka.product.entity.Product;
import com.czerniecka.product.dto.ProductMapper;
import com.czerniecka.product.repository.ProductRepository;
import com.czerniecka.product.vo.Inventory;
import com.czerniecka.product.vo.InventoryRequest;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.runner.RunWith;
import org.mapstruct.factory.Mappers;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ProductServiceTest {


    @Mock
    ProductRepository productRepository;
    @Spy
    ProductMapper productMapper = Mappers.getMapper(ProductMapper.class);
    @Mock
    RestTemplate restTemplate;

    ProductService productService;

    @Before
    public void init() {
        productService = new ProductService(productRepository, productMapper, restTemplate);
    }


    @Test
    public void testMapper(){
        Assertions.assertNotNull(productMapper);
    }

    @Test
    public void shouldMapProductToProductDTO(){

        Product product = new Product(UUID.randomUUID(), "Product", BigDecimal.ONE, BigDecimal.TEN,
                "Category", LocalDateTime.now(), LocalDateTime.now(), UUID.randomUUID());

        ProductDTO productDTO = productMapper.toProductDTO(product);

        assertThat(productDTO.getName().equals("Product"));
        assertThat(productDTO.getCategory().equals("Category"));

    }

    @Test
    public void shouldReturnAllProducts() {

        List<Product> products = new ArrayList<>();
        Product product = new Product(UUID.randomUUID(), "Product", BigDecimal.ONE, BigDecimal.TEN,
                "Category", LocalDateTime.now(), LocalDateTime.now(), UUID.randomUUID());
        Product product2 = new Product(UUID.randomUUID(), "Product2", BigDecimal.ONE, BigDecimal.TEN,
                "Category2", LocalDateTime.now(), LocalDateTime.now(), UUID.randomUUID());
        products.add(product);
        products.add(product2);

        when(productRepository.findAll()).thenReturn(products);

        List<ProductDTO> all = productService.findAll();
        Assertions.assertEquals(2, all.size());

    }

    @Test
    public void shouldReturnOptionalEmpty(){

Optional<ProductDTO> productById = productService.findProductById(UUID.randomUUID());
        Assertions.assertEquals(Optional.empty(), productById);
    }

    @Test
    public void shouldReturnSavedObject(){

        Product product = new Product(UUID.randomUUID(), "Product", BigDecimal.ONE, BigDecimal.TEN,
                "Category", LocalDateTime.now(), LocalDateTime.now(), UUID.randomUUID());

        ProductDTO productDTO = new ProductDTO();

        when(productRepository.save(productMapper.toProduct(productDTO))).thenReturn(product);
        ProductDTO saved = productService.save(new InventoryRequest(new Inventory(), productDTO));

        assertThat(saved).isNotNull();
        assertThat(saved.getCategory()).isEqualTo("Category");
        assertThat(saved.getName()).isEqualTo("Product");

    }
}