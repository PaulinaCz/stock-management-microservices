package com.czerniecka.product.controller;

import com.czerniecka.product.dto.ProductDTO;
import com.czerniecka.product.service.ProductService;
import com.czerniecka.product.vo.ProductSupplierResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

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

    @GetMapping(value = "", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<ProductDTO> getAllProducts() {
        Flux<ProductDTO> products = productService.findAll();
        return products;
    }

    @GetMapping("/{id}")
    public ResponseEntity<Mono<ProductDTO>> getProductById(@PathVariable("id") String productId) {
        Mono<ProductDTO> product = productService.findProductById(productId);

        HttpStatus status = product != null ? HttpStatus.OK : HttpStatus.NOT_FOUND;

        return new ResponseEntity<>(product, status);
    }

    @GetMapping("/category/{category}")
    public Flux<ProductDTO> getProductsWhereCategoryContains(@PathVariable String category) {
        return productService.findProductsWhereCategoryContains(category);
    }

    @GetMapping("/supplier/{supplierId}")
    public Flux<ProductDTO> getProductsBySupplier(@PathVariable String supplierId) {
        return productService.findProductsBySupplier(supplierId);
    }

    @GetMapping("/{productId}/supplier")
    public ResponseEntity<Mono<ProductSupplierResponse>> getProductWithSupplier(@PathVariable String productId) {
        Mono<ProductSupplierResponse> productWithSupplier = productService.getProductWithSupplier(productId);

        HttpStatus status = productWithSupplier != null ? HttpStatus.OK : HttpStatus.NOT_FOUND;

        return new ResponseEntity<>(productWithSupplier, status);
    }

    @PostMapping("")
    public ResponseEntity<Mono<ProductDTO>> addProduct(@Valid @RequestBody ProductDTO productDTO) {
        Mono<ProductDTO> saved = productService.save(productDTO);

        HttpStatus status = saved != null ? HttpStatus.CREATED : HttpStatus.SERVICE_UNAVAILABLE;

        return new ResponseEntity<>(saved, status);
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
