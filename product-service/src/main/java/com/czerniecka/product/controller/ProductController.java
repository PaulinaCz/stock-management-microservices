package com.czerniecka.product.controller;

import com.czerniecka.product.dto.ProductDTO;
import com.czerniecka.product.service.ProductService;
import com.czerniecka.product.vo.InventoryRequest;
import com.czerniecka.product.vo.ResponseTemplateVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/products")
public class ProductController {

    private final ProductService productService;

    @Autowired
    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping("")
    public ResponseEntity<List<ProductDTO>> getAllProducts() {
        List<ProductDTO> products = productService.findAll();

        return ResponseEntity.ok(products);

    }

    @GetMapping("/{productId}")
    public ResponseEntity<ProductDTO> getProductById(@PathVariable UUID productId) {
        Optional<ProductDTO> product = productService.findProductById(productId);

        return product.map(productDTO -> new ResponseEntity<>(productDTO, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @GetMapping("/{productId}/supplier")
    public ResponseEntity getProductWithSupplier(@PathVariable UUID productId) {
        Optional<ResponseTemplateVO> productWithSupplier = productService.getProductWithSupplier(productId);

        return productWithSupplier.map(responseTemplateVO -> new ResponseEntity(responseTemplateVO, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity(HttpStatus.NOT_FOUND));

    }

    @GetMapping("/category/{category}")
    public ResponseEntity<List<ProductDTO>> getProductsByCategory(@PathVariable String category) {
        List<ProductDTO> productsByCategory = productService.findProductsByCategory(category);
        return ResponseEntity.ok(productsByCategory);
    }

    @GetMapping("/supplier/{supplierId}")
    public ResponseEntity<List<ProductDTO>> getProductsBySupplier(@PathVariable UUID supplierId) {
        List<ProductDTO> productsBySupplier = productService.findProductsBySupplier(supplierId);
        return ResponseEntity.ok(productsBySupplier);
    }

    @PostMapping("")
    public ResponseEntity<ProductDTO> addProduct(@RequestBody InventoryRequest request) {
        Optional<ProductDTO> saved = productService.save(request);
        return saved.map(product -> new ResponseEntity<>(product, HttpStatus.CREATED))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.SERVICE_UNAVAILABLE));
    }

}
