package com.czerniecka.product.controller;

import com.czerniecka.product.entity.Product;
import com.czerniecka.product.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
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
    public List<Product> getAllProducts(){
        return productService.findAll();
    }

    @GetMapping("/{productId}")
    public Product getProductById(@PathVariable UUID productId){
        Optional<Product> productById = productService.findProductById(productId);

        return productById.orElse(null);
    }

    @GetMapping("/category/{category}")
    public List<Product> getProductsByCategory(@PathVariable String category){
        return productService.findProductsByCategory(category);
    }

    @GetMapping("/supplier/{supplierId}")
    public List<Product> getProductsBySupplier(@PathVariable UUID supplierId){
        return productService.findProductsBySupplier(supplierId);
    }

    @PostMapping("")
    public Product addProduct(@RequestBody Product product){
        return productService.save(product);
    }

    @PutMapping("product/{productId}")
    public void updateProduct(@PathVariable UUID productId
                            , @RequestBody Product product){

        productService.updateProduct(productId, product);
    }
}
