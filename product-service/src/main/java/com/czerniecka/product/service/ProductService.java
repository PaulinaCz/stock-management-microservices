package com.czerniecka.product.service;

import com.czerniecka.product.controller.NotFoundException;
import com.czerniecka.product.dto.ProductDTO;
import com.czerniecka.product.dto.ProductMapper;
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
    private final ProductMapper productMapper;
    private RestTemplate restTemplate;

    @Autowired
    public ProductService(ProductRepository productRepository, ProductMapper productMapper, RestTemplate restTemplate) {
        this.productRepository = productRepository;
        this.productMapper = productMapper;
        this.restTemplate = restTemplate;
    }

    public List<ProductDTO> findAll() {

        List<Product> all = productRepository.findAll();
        return productMapper.toProductsDTOs(all);
    }

    public ProductDTO findProductById(UUID productId) {

        Optional<Product> byId = productRepository.findById(productId);

        return byId.map(productMapper::toProductDTO).orElse(null);

    }

    public ProductDTO save(ProductDTO productDTO) {
        Product product = productMapper.toProduct(productDTO);
        Product saved = productRepository.save(product);
        return productMapper.toProductDTO(saved);
    }

    public List<ProductDTO> findProductsByCategory(String category) {

        List<Product> allByCategory = productRepository.findAllByCategory(category);
        return productMapper.toProductsDTOs(allByCategory);
    }

    public void updateProduct(UUID productId, ProductDTO productDTO) {

        Product p = productRepository.findById(productId).orElseThrow(NotFoundException::new);

        p.setName(productDTO.getName());
        p.setBuyingPrice(productDTO.getBuyingPrice());
        p.setSellingPrice(productDTO.getSellingPrice());
        p.setSupplierId(productDTO.getSupplierId());
        p.setLastModified(LocalDateTime.now());
        p.setCategory(productDTO.getCategory());

        productRepository.save(p);
    }

    public List<ProductDTO> findProductsBySupplier(UUID supplierId) {

        List<Product> allBySupplierId = productRepository.findAllBySupplierId(supplierId);
        return productMapper.toProductsDTOs(allBySupplierId);
    }

    public ResponseTemplateVO getProductWithSupplier(UUID productId) {
        ResponseTemplateVO vo = new ResponseTemplateVO();
        Product product = productRepository.findProductById(productId);

        Supplier supplier = restTemplate.getForObject("http://localhost:3003/suppliers/" + product.getSupplierId(),
                                                        Supplier.class);
        vo.setProductDTO(productMapper.toProductDTO(product));
        vo.setSupplier(supplier);

        return vo;
    }
}
