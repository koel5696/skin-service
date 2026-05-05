package com.swyp3.skin.domain.product.repository;

import com.swyp3.skin.domain.product.domain.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {

    @Query("""
        SELECT p FROM Product p
        WHERE p.active = true
            AND (
                LOWER(p.name) LIKE LOWER(CONCAT('%', :keyword, '%'))
                OR LOWER(p.brand) LIKE LOWER(CONCAT('%', :keyword, '%'))
            )
        """)
    List<Product> search(String keyword);
    List<Product> findAllByActiveTrue();

    long countByActiveTrue();
}
