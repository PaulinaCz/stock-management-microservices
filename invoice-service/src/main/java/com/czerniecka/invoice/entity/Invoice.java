package com.czerniecka.invoice.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.Type;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Invoice {

    @Id
    @GeneratedValue
    @Type(type="org.hibernate.type.UUIDCharType")
    private UUID id;
    
    @Type(type="org.hibernate.type.UUIDCharType")
    private UUID productId;
    private int amount;
    @CreationTimestamp
    private LocalDateTime datePlaced;

    @Type(type="org.hibernate.type.UUIDCharType")
    private UUID supplierId;
}
