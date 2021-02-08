package com.czerniecka.product.service;

import com.czerniecka.product.client.InventoryServiceClient;
import com.czerniecka.product.client.SupplierServiceClient;
import com.czerniecka.product.dto.ProductDTO;
import com.czerniecka.product.dto.ProductMapper;
import com.czerniecka.product.entity.Product;
import com.czerniecka.product.repository.ProductRepository;
import com.czerniecka.product.vo.Inventory;
import com.czerniecka.product.vo.ProductWithSupplierResponse;
import com.czerniecka.product.vo.Supplier;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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
    public ProductService(ProductRepository productRepository, ProductMapper productMapper,
                          SupplierServiceClient supplierServiceClient, InventoryServiceClient inventoryServiceClient) {
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

    public List<ProductDTO> findProductsWhereCategoryContains(String category) {

        List<Product> allByCategory = productRepository.findProductByCategoryContaining(category);
        return productMapper.toProductsDTOs(allByCategory);
    }

    public List<ProductDTO> findProductsBySupplier(UUID supplierId) {

        List<Product> allBySupplierId = productRepository.findAllBySupplierId(supplierId);
        return productMapper.toProductsDTOs(allBySupplierId);
    }


    /* If supplier-service is unavailable, returns Product with empty Supplier object*/
    public Optional<ProductWithSupplierResponse> getProductWithSupplier(UUID productId) {
        ProductWithSupplierResponse response = new ProductWithSupplierResponse();
        Optional<Product> product = productRepository.findById(productId);
        if (product.isPresent()) {
            Supplier supplier = supplierServiceClient.getSupplier(product.get().getSupplierId());
            response.setProduct(productMapper.toProductDTO(product.get()));
            response.setSupplier(supplier);
            return Optional.of(response);
        } else {
            return Optional.empty();
        }
    }

    public Optional<ProductDTO> save(ProductDTO productDTO) {

        Product product = productMapper.toProduct(productDTO);
        Product saved = productRepository.save(product);

        /* Creates inventory - SET productId equal saved Product id and quantity equal 0 */
        Inventory inventory = new Inventory();
        inventory.setProductId(saved.getId());
        inventory.setQuantity(0);
        HttpStatus httpStatus = inventoryServiceClient.postInventory(inventory);

        /* If unable to create new inventory -> Product will not be saved*/
        if(httpStatus.equals(HttpStatus.SERVICE_UNAVAILABLE)){
            productRepository.delete(product);
            return Optional.empty();
        }else{
            return Optional.of(productMapper.toProductDTO(saved));
        }

    }

}
