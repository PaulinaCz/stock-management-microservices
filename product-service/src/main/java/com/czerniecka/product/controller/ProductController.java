package com.czerniecka.product.controller;

import com.czerniecka.product.dto.ProductDTO;
import com.czerniecka.product.service.ProductService;
import com.czerniecka.product.vo.ResponseTemplateVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
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
    public List<ProductDTO> getAllProducts(){
        return productService.findAll();
    }

    @GetMapping("/{productId}")
    public ProductDTO getProductById(@PathVariable UUID productId){
        return productService.findProductById(productId);
    }

    @GetMapping("/withSupplier/{productId}")
    public ResponseTemplateVO getProductWithSupplier(@PathVariable UUID productId){
        return productService.getProductWithSupplier(productId);
    }

    @GetMapping("/category/{category}")
    public List<ProductDTO> getProductsByCategory(@PathVariable String category){
        return productService.findProductsByCategory(category);
    }

    @GetMapping("/supplier/{supplierId}")
    public List<ProductDTO> getProductsBySupplier(@PathVariable UUID supplierId){
        return productService.findProductsBySupplier(supplierId);
    }

    @PostMapping("")
    public ProductDTO addProduct(@RequestBody ProductDTO productDTO){
        return productService.save(productDTO);
    }

    @PutMapping("product/{productId}")
    public void updateProduct(@PathVariable UUID productId
                            , @RequestBody ProductDTO productDTO){

        productService.updateProduct(productId, productDTO);
    }
}
