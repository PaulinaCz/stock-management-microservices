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

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final ProductMapper productMapper;
    private final SupplierServiceClient supplierServiceClient;
    private final InventoryServiceClient inventoryServiceClient;

    @Autowired
    public ProductService(ProductRepository productRepository, ProductMapper productMapper, SupplierServiceClient supplierServiceClient, InventoryServiceClient inventoryServiceClient) {
        this.productRepository = productRepository;
        this.productMapper = productMapper;
        this.supplierServiceClient = supplierServiceClient;
        this.inventoryServiceClient = inventoryServiceClient;
    }

    public List<ProductDTO> findAll() {

        List<Product> all = productRepository.findAll();
        return productMapper.toProductsDTOs(all);
    }

    public Optional<ProductDTO> findProductById(UUID productId) {

        Optional<Product> byId = productRepository.findById(productId);
        return byId.map(productMapper::toProductDTO);

    }

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
            Supplier supplier = supplierServiceClient.getSupplier(product.get().getSupplierId());
            vo.setProduct(productMapper.toProductDTO(product.get()));
            vo.setSupplier(supplier);
            return Optional.of(vo);
        } else {
            return Optional.empty();
        }
    }

    //TODO: POST needs to be TRANSACTIONAL!

    public ProductDTO save(InventoryRequest request) {
        Product product = productMapper.toProduct(request.getProduct());
        Product saved = productRepository.save(product);

        Inventory inventory = request.getInventory();
        inventory.setProductId(saved.getId());
        inventory.setQuantity(0);
        inventoryServiceClient.postInventory(inventory);

        return productMapper.toProductDTO(saved);
    }

}
