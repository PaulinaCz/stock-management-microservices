package com.czerniecka.product.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Type;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;


@Entity
@Data
@NoArgsConstructor
public class Product {

    @Id
    @GeneratedValue
    @Type(type="org.hibernate.type.UUIDCharType")
    private UUID id;
    private String name;
    private String description;
    private BigDecimal buyingPrice;
    private BigDecimal sellingPrice;
    private String category;
    private LocalDateTime lastModified;

    private UUID supplierId;

}
