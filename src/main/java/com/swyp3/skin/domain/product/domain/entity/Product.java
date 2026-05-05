package com.swyp3.skin.domain.product.domain.entity;

import com.swyp3.skin.domain.product.domain.enums.ProductCategory;
import com.swyp3.skin.domain.product.domain.enums.ProductUsageTime;
import com.swyp3.skin.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Product extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    private String brand;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ProductCategory category;

    @Enumerated(EnumType.STRING)
    @Column(length = 10, nullable = false)
    private ProductUsageTime productUsageTime;

    @Column(nullable = false)
    private int price;

    @Column(length = 1000)
    private String description;

    @Column(length = 500)
    private String imageUrl;

    @Column(length = 1000)
    private String purchaseUrl;

    @Column(nullable = false)
    private Boolean active;

    public static Product create(
            String name,
            String brand,
            ProductCategory category,
            ProductUsageTime productUsageTime,
            int price,
            String description,
            String imageUrl,
            String purchaseUrl,
            boolean active
    ){
        Product product = new Product();
        product.name = name;
        product.brand = brand;
        product.category = category;
        product.productUsageTime = productUsageTime;
        product.price = price;
        product.description = description;
        product.imageUrl = imageUrl;
        product.purchaseUrl = purchaseUrl;
        product.active = active;
        return product;
    }

    public void update(
            String name,
            String brand,
            ProductCategory category,
            ProductUsageTime productUsageTime,
            int price,
            String description,
            String imageUrl,
            String purchaseUrl,
            boolean active
    ) {
        this.name = name;
        this.brand = brand;
        this.category = category;
        this.productUsageTime = productUsageTime;
        this.price = price;
        this.description = description;
        this.imageUrl = imageUrl;
        this.purchaseUrl = purchaseUrl;
        this.active = active;
    }
}
