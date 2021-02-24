package com.czerniecka.product.service;

import com.czerniecka.product.client.InventoryServiceClient;
import com.czerniecka.product.client.SupplierServiceClient;
import com.czerniecka.product.dto.CreateProductDTO;
import com.czerniecka.product.dto.ProductDTO;
import com.czerniecka.product.dto.ProductMapper;
import com.czerniecka.product.entity.Product;
import com.czerniecka.product.repository.ProductRepository;
import com.czerniecka.product.vo.Inventory;
import com.czerniecka.product.vo.ProductSupplierResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;


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

    public Flux<ProductDTO> findAll() {

        Flux<Product> all = productRepository.findAll();
        return all.map(productMapper::toProductDTO);
    }

    public Mono<ProductDTO> findProductById(String productId) {

        Mono<Product> byId = productRepository.findById(productId);
        return byId.map(productMapper::toProductDTO);

    }

    public Flux<ProductDTO> findProductsWhereCategoryContains(String category) {

        Flux<Product> allByCategory = productRepository.findProductByCategoryContaining(category);

        return allByCategory.map(productMapper::toProductDTO);

    }

    public Flux<ProductDTO> findProductsBySupplier(String supplierId) {

        Flux<Product> allBySupplierId = productRepository.findAllBySupplierId(supplierId);

        return allBySupplierId.map(productMapper::toProductDTO);
    }

    /**
     * If supplier-service is not available method returns:
     * Product with empty Supplier object
     * <p>
     * If product is not found returns Mono.empty
     */
    public Mono<ProductSupplierResponse> getProductWithSupplier(String productId) {
        var response = new ProductSupplierResponse();
        Mono<Product> product = productRepository.findById(productId);
        return product.switchIfEmpty(Mono.empty())
                .flatMap(p ->{
                    response.setProduct(productMapper.toProductDTO(p));
                    response.setSupplier(supplierServiceClient.getSupplier(p.getSupplierId()));
                    return Mono.just(response);
                });
    }
    
    public Mono<ProductDTO> save(CreateProductDTO productDTO) {

        Product product = productMapper.toCreateProduct(productDTO);

        Inventory inventory = new Inventory();
        inventory.setProductId(product.getId());
        inventory.setQuantity(0);

            Mono<Inventory> inventoryMono = inventoryServiceClient.postInventory(inventory);

            return inventoryMono.switchIfEmpty(Mono.empty())
                    .flatMap(i -> productRepository.save(product).map(productMapper::toProductDTO));

    }
}
