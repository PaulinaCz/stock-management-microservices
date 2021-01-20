package com.czerniecka.product.service;

import com.czerniecka.product.controller.NotFoundException;
import com.czerniecka.product.entity.Product;
import com.czerniecka.product.repository.ProductRepository;
import com.czerniecka.product.vo.ResponseTemplateVO;
import com.czerniecka.product.vo.Supplier;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class ProductService {

    private final ProductRepository productRepository;

    private RestTemplate restTemplate;

    @Autowired
    public ProductService(ProductRepository productRepository, RestTemplate restTemplate) {
        this.productRepository = productRepository;
        this.restTemplate = restTemplate;
    }

    public List<Product> findAll() {
        return productRepository.findAll();
    }

    public Optional<Product> findProductById(UUID productId) {
        return productRepository.findById(productId);
    }

    public Product save(Product product) {

        return productRepository.save(product);
    }

    public List<Product> findProductsByCategory(String category) {

        return productRepository.findAllByCategory(category);
    }

    public void updateProduct(UUID productId, Product product) {

        Product p = productRepository.findById(productId).orElseThrow(NotFoundException::new);

        p.setBuyingPrice(product.getBuyingPrice());
        p.setSellingPrice(product.getSellingPrice());
        p.setSupplierId(product.getSupplierId());
        p.setLastModified(LocalDateTime.now());
        p.setCategory(product.getCategory());
        p.setDescription(product.getDescription());

        productRepository.save(p);
    }

    public List<Product> findProductsBySupplier(UUID supplierId) {

        return productRepository.findAllBySupplierId(supplierId);
    }

    public ResponseTemplateVO getProductWithSupplier(UUID productId) {
        ResponseTemplateVO vo = new ResponseTemplateVO();
        Product product = productRepository.findProductById(productId);

        Supplier supplier = restTemplate.getForObject("http://localhost:3003/suppliers/" + product.getSupplierId(),
                                                        Supplier.class);
        vo.setProduct(product);
        vo.setSupplier(supplier);

        return vo;
    }
}
