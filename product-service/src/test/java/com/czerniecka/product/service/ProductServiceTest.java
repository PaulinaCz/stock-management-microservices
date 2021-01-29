package com.czerniecka.product.service;

import com.czerniecka.product.dto.ProductDTO;
import com.czerniecka.product.dto.ProductMapper;
import com.czerniecka.product.entity.Product;
import com.czerniecka.product.repository.ProductRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
@ExtendWith(MockitoExtension.class)
public class ProductServiceTest {


    @Mock
    ProductRepository productRepository;
    @Mock
    ProductMapper productMapper;

    @InjectMocks
    ProductService productService;

    @Before
    public void setUp() {
        when(productMapper.toProductDTO(any(Product.class))).thenReturn(new ProductDTO());
    }


    @Test
    public void testMapper(){
        Assertions.assertNotNull(productMapper);
    }

    @Test
    public void shouldReturnAll() {

        List<Product> products = new ArrayList<>();
        Product product = new Product(UUID.randomUUID(), "Product", BigDecimal.ONE, BigDecimal.TEN,
                "Category", LocalDateTime.now(), LocalDateTime.now(), UUID.randomUUID());
        Product product2 = new Product(UUID.randomUUID(), "Product2", BigDecimal.ONE, BigDecimal.TEN,
                "Category2", LocalDateTime.now(), LocalDateTime.now(), UUID.randomUUID());
        products.add(product);
        products.add(product2);

        when(productRepository.findAll()).thenReturn(products);

        List<ProductDTO> all = productService.findAll();

        Assertions.assertEquals(3, all.size());

    }

}