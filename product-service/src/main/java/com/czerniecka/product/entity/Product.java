package com.czerniecka.product.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;


import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Document(collection = "products")
@Data
@NoArgsConstructor
public class Product {

    @Id
    private String id;
    private String name;
    private BigDecimal buyingPrice;
    private BigDecimal sellingPrice;
    private String category;

    @CreatedDate
    private LocalDateTime createdOn;

    @LastModifiedDate
    private LocalDateTime lastModified;

    private String supplierId;

}
