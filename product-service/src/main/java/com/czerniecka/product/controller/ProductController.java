package com.czerniecka.product.controller;

import com.czerniecka.product.dto.ProductDTO;
import com.czerniecka.product.service.ProductService;
import com.czerniecka.product.vo.ProductSupplierResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

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

    @GetMapping("/category/{category}")
    public ResponseEntity<List<ProductDTO>> getProductsWhereCategoryContains(@PathVariable String category) {
        List<ProductDTO> productsByCategory = productService.findProductsWhereCategoryContains(category);
        return ResponseEntity.ok(productsByCategory);
    }

    @GetMapping("/supplier/{supplierId}")
    public ResponseEntity<List<ProductDTO>> getProductsBySupplier(@PathVariable UUID supplierId) {
        List<ProductDTO> productsBySupplier = productService.findProductsBySupplier(supplierId);
        return ResponseEntity.ok(productsBySupplier);
    }

    @GetMapping("/{productId}/supplier")
    public ResponseEntity getProductWithSupplier(@PathVariable UUID productId) {
        Optional<ProductSupplierResponse> productWithSupplier = productService.getProductWithSupplier(productId);

        return productWithSupplier
                .map(productSupplierResponse -> new ResponseEntity(productSupplierResponse, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity(HttpStatus.NOT_FOUND));
    }

    @PostMapping("")
    public ResponseEntity<ProductDTO> addProduct(@Valid @RequestBody ProductDTO productDTO) {
        Optional<ProductDTO> saved = productService.save(productDTO);
        return saved.map(product -> new ResponseEntity<>(product, HttpStatus.CREATED))
                .orElseGet(() -> new ResponseEntity("Error while creating product inventory. Product not saved.",
                        HttpStatus.SERVICE_UNAVAILABLE));
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Map<String, Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex) {

        Map<String, Object> errorBody = new HashMap<>();

        errorBody.put("timestamp", LocalDateTime.now());

        List<String> errors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .collect(Collectors.toList());

        errorBody.put("validationErrors", errors);
        return errorBody;
    }

}
