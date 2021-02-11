package com.czerniecka.product.service;

import com.czerniecka.product.client.InventoryServiceClient;
import com.czerniecka.product.client.SupplierServiceClient;
import com.czerniecka.product.dto.ProductDTO;
import com.czerniecka.product.dto.ProductMapper;
import com.czerniecka.product.entity.Product;
import com.czerniecka.product.repository.ProductRepository;
import com.czerniecka.product.vo.Inventory;
import com.czerniecka.product.vo.ProductSupplierResponse;
import com.czerniecka.product.vo.Supplier;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

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
        return Flux.just(productMapper.toProductsDTOs(all));
    }

    public Mono<ProductDTO> findProductById(String productId) {

        Mono<Product> byId = productRepository.findById(productId);
        return byId.map(productMapper::toProductDTO);

    }

    public Flux<ProductDTO> findProductsWhereCategoryContains(String category) {

        Flux<Product> allByCategory = productRepository.findProductByCategoryContaining(category);

        return Flux.just(productMapper.toProductsDTOs(allByCategory));

    }

    public Flux<ProductDTO> findProductsBySupplier(String supplierId) {

        Flux<Product> allBySupplierId = productRepository.findAllBySupplierId(supplierId);

        return Flux.just(productMapper.toProductsDTOs(allBySupplierId));
    }


    /* If supplier-service is unavailable, returns Product with empty Supplier object*/
    public Mono<ProductSupplierResponse> getProductWithSupplier(String productId) {
        ProductSupplierResponse response = new ProductSupplierResponse();
        Mono<Product> product = productRepository.findById(productId);
        return product.flatMap(p -> {
            Supplier supplier = supplierServiceClient.getSupplier(p.getSupplierId());
            response.setProduct(productMapper.toProductDTO(p));
            response.setSupplier(supplier);
            return Mono.just(response);
        })
                .or(Mono.empty());
    }

    public Mono<ProductDTO> save(ProductDTO productDTO) {

        Product product = productMapper.toProduct(productDTO);
        Mono<Product> saved = productRepository.save(product);

        /* Creates inventory - SET productId equal saved Product id and quantity equal 0 */
        return saved.flatMap(p -> {
            Inventory inventory = new Inventory();
            inventory.setProductId(p.getId());
            inventory.setQuantity(0);

            HttpStatus httpStatus = inventoryServiceClient.postInventory(inventory);
            /* If unable to create new inventory -> Product will not be saved*/
            if(httpStatus.equals(HttpStatus.SERVICE_UNAVAILABLE)){
                productRepository.delete(p);
                return Mono.empty();
            }else{
                return Mono.just(productMapper.toProductDTO(p));
            }
        }).or(Mono.empty());
    }

}
