package com.swyp3.skin.admin.product.service;

import com.swyp3.skin.admin.product.dto.AdminProductCreateForm;
import com.swyp3.skin.admin.product.dto.AdminProductUpdateForm;
import com.swyp3.skin.domain.product.domain.entity.Product;
import com.swyp3.skin.domain.product.domain.entity.ProductGroupScore;
import com.swyp3.skin.domain.product.domain.enums.ProductCategory;
import com.swyp3.skin.domain.product.domain.enums.ProductUsageTime;
import com.swyp3.skin.domain.product.repository.ProductGroupScoreRepository;
import com.swyp3.skin.domain.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class AdminProductService {

    private final ProductRepository productRepository;
    private final ProductGroupScoreRepository productGroupScoreRepository;

    @Transactional(readOnly = true)
    public List<Product> findAll() {
        return productRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Product findById(Long productId){
        return productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("상품 없음"));
    }

    @Transactional
    public Long update(Long productId, AdminProductUpdateForm form) {
        Product product = findById(productId);
        validateSunCareUsageTime(form.category(), form.productUsageTime());
        product.update(
                form.name(),
                form.brand(),
                form.category(),
                form.productUsageTime(),
                form.price(),
                form.description(),
                form.imageUrl(),
                form.purchaseUrl(),
                form.active()
        );
        return product.getId();
    }


    @Transactional
    public Long delete(Long productId) {
        Product findProduct = findById(productId);
        productGroupScoreRepository.deleteByProductId(productId);
        productRepository.delete(findProduct);
        return productId;
    }


    @Transactional
    public Long create(AdminProductCreateForm form) {
        validateGroupScores(form.groupScores());
        validateSunCareUsageTime(form.category(), form.productUsageTime());

        Product product = Product.create(
                form.name(),
                form.brand(),
                form.category(),
                form.productUsageTime(),
                form.price(),
                form.description(),
                form.imageUrl(),
                form.purchaseUrl(),
                form.active()
        );
        productRepository.save(product);

        List<ProductGroupScore> scores = List.of(
                ProductGroupScore.create(product, form.groupScores().get(0).ingredientGroup(), form.groupScores().get(0).score(), 1),
                ProductGroupScore.create(product, form.groupScores().get(1).ingredientGroup(), form.groupScores().get(1).score(), 2),
                ProductGroupScore.create(product, form.groupScores().get(2).ingredientGroup(), form.groupScores().get(2).score(), 3)
        );
        productGroupScoreRepository.saveAll(scores);

        return product.getId();
    }

    private void validateGroupScores(List<AdminProductCreateForm.GroupScoreForm> groupScores) {
        Set<Object> unique = new HashSet<>();
        for (AdminProductCreateForm.GroupScoreForm gs : groupScores) {
            if (!unique.add(gs.ingredientGroup())) {
                throw new IllegalArgumentException("성분군은 중복될 수 없습니다.");
            }
        }
    }

    private void validateSunCareUsageTime(ProductCategory category, ProductUsageTime productUsageTime) {
        if (category == ProductCategory.SUN_CARE && productUsageTime != ProductUsageTime.AM) {
            throw new IllegalArgumentException("선크림은 루틴 시간대를 AM으로만 설정할 수 있습니다.");
        }
    }
}
