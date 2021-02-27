package com.czerniecka.product.controller;

import com.czerniecka.product.dto.CreateProductDTO;
import com.czerniecka.product.dto.ProductDTO;
import com.czerniecka.product.exceptions.ProductNotFound;
import com.czerniecka.product.service.ProductService;
import com.czerniecka.product.vo.ProductSupplierResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.naming.ServiceUnavailableException;
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

    @GetMapping(value = "", produces = "application/json")
    public Flux<ProductDTO> getAllProducts() {
        return productService.findAll();
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/{id}")
    public Mono<ProductDTO> getProductById(@PathVariable("id") String productId) {

        return productService.findProductById(productId)
                .switchIfEmpty(Mono.error(new ProductNotFound(productId)));
    }

    @GetMapping("/category/{category}")
    public Flux<ProductDTO> getProductsWhereCategoryContains(@PathVariable String category) {
        return productService.findProductsWhereCategoryContains(category);
    }

    @GetMapping("/supplier/{id}")
    public Flux<ProductDTO> getProductsBySupplier(@PathVariable("id") String supplierId) {
        return productService.findProductsBySupplier(supplierId);
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/{id}/supplier")
    public Mono<ProductSupplierResponse> getProductWithSupplier(@PathVariable("id") String productId) {

        return productService.getProductWithSupplier(productId)
                .switchIfEmpty(Mono.error(new ProductNotFound(productId)));
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("")
    public Mono<ProductDTO> addProduct(@Valid @RequestBody CreateProductDTO productDTO) {
        
        return productService.save(productDTO);
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(ProductNotFound.class)
    public Map<String, Object> handleNotFound(ProductNotFound e){
        Map<String, Object> errorBody = new HashMap<>();

        errorBody.put("timestamp", LocalDateTime.now());
        errorBody.put("error", "Product " + e.getMessage() + " not found");

        return errorBody;
    }

    @ResponseStatus(HttpStatus.SERVICE_UNAVAILABLE)
    @ExceptionHandler(ServiceUnavailableException.class)
    public Map<String, Object> handleServiceNotAvailable(ServiceUnavailableException e){
        Map<String, Object> errorBody = new HashMap<>();

        errorBody.put("timestamp", LocalDateTime.now());
        errorBody.put("error", e.getMessage());

        return errorBody;
    }
    
    //TODO: fix input validation & exception handler
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
