package com.intuit.auction.service.model;

import java.util.UUID;

import com.intuit.auction.service.enums.ProductCategory;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "product")
@Data
public class Product {
    @Id
    private String productId = UUID.randomUUID().toString();
    private String productName;

    @Enumerated(EnumType.STRING)
    private ProductCategory productCategory;

    private double productBasePrice;
    private String description;
}
