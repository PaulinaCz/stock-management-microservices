package com.czerniecka.product.service;

import com.czerniecka.product.dto.ProductDTO;
import com.czerniecka.product.dto.ProductMapper;
import com.czerniecka.product.entity.Product;
import com.czerniecka.product.repository.ProductRepository;
import com.czerniecka.product.vo.Inventory;
import com.czerniecka.product.vo.InventoryRequest;
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

//    public Optional<ProductDTO> findProductById(UUID productId) {
//
//        Optional<Product> byId = productRepository.findById(productId);
//        return byId.map(productMapper::toProductDTO);
//
//    }


    public List<ProductDTO> findProductsByCategory(String category) {

        List<Product> allByCategory = productRepository.findAllByCategory(category);
        return productMapper.toProductsDTOs(allByCategory);
    }

    public List<ProductDTO> findProductsBySupplier(UUID supplierId) {

        List<Product> allBySupplierId = productRepository.findAllBySupplierId(supplierId);
        return productMapper.toProductsDTOs(allBySupplierId);
    }

    public Optional<ResponseTemplateVO> getProductWithSupplier(UUID productId) {
        ResponseTemplateVO vo = new ResponseTemplateVO();
        Optional<Product> product = productRepository.findById(productId);

        if (product.isPresent()) {
            Supplier supplier = restTemplate.getForObject("http://localhost:3003/suppliers/" + product.get().getSupplierId(),
                    Supplier.class);
            vo.setProduct(productMapper.toProductDTO(product.get()));
            vo.setSupplier(supplier);
            return Optional.of(vo);
        } else {
            return Optional.empty();
        }
    }

    public ProductDTO save(InventoryRequest request) {
        Product product = productMapper.toProduct(request.getProduct());
        Product saved = productRepository.save(product);

        Inventory inventory = request.getInventory();
        inventory.setProductId(product.getId());
        inventory.setQuantity(0);
        restTemplate.postForObject("http://localhost:3005/inventory", inventory, Inventory.class);

        return productMapper.toProductDTO(saved);
    }

//    public boolean updateProduct(UUID productId, ProductDTO productDTO) {
//
//        Optional<Product> p = productRepository.findById(productId);
//
//        if (p.isPresent()) {
//            Product product = p.get();
//            product.setName(productDTO.getName());
//            product.setBuyingPrice(productDTO.getBuyingPrice());
//            product.setSellingPrice(productDTO.getSellingPrice());
//            product.setSupplierId(productDTO.getSupplierId());
//            product.setLastModified(LocalDateTime.now());
//            product.setCategory(productDTO.getCategory());
//            productRepository.save(product);
//
//            return true;
//        } else {
//            return false;
//        }
//
//    }

}
