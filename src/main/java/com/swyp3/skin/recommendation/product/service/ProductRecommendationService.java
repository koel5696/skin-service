package com.swyp3.skin.recommendation.product.service;

import com.swyp3.skin.domain.common.enums.IngredientGroup;
import com.swyp3.skin.domain.product.domain.entity.Product;
import com.swyp3.skin.domain.product.domain.enums.ProductCategory;
import com.swyp3.skin.domain.product.repository.ProductGroupScoreRepository;
import com.swyp3.skin.domain.product.repository.ProductRepository;
import com.swyp3.skin.domain.skinresult.domain.entity.SkinResultGroupScore;
import com.swyp3.skin.domain.skinresult.repository.SkinResultGroupScoreRepository;
import com.swyp3.skin.recommendation.product.calculator.ProductScoreCalculator;
import com.swyp3.skin.recommendation.product.dto.ProductScore;
import com.swyp3.skin.recommendation.product.dto.ProductSupply;
import com.swyp3.skin.recommendation.product.dto.RecommendedProduct;
import com.swyp3.skin.recommendation.product.mapper.ProductVectorMapper;
import com.swyp3.skin.recommendation.product.policy.ProductFilterPolicy;
import jdk.jfr.Category;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductRecommendationService {

    private final ProductRepository productRepository;
    private final ProductGroupScoreRepository productGroupScoreRepository;
    private final SkinResultGroupScoreRepository skinResultGroupScoreRepository;

    private final ProductVectorMapper productVectorMapper;
    private final ProductFilterPolicy productFilterPolicy;
    private final ProductScoreCalculator productScoreCalculator;

    public List<RecommendedProduct> recommend(Long resultId) {

        // 1. Need 어떤 성분 얼마나 필요한지 조회
        Map<IngredientGroup, Double> need = loadNeed(resultId);

        // 2. Product 추천 후보 조회
        List<Product> products = productRepository.findAll();

        // 3. Supply 생성 해당 제품이 어떤 성분 얼마나 공급하는지
        List<ProductSupply> supplies =
                productVectorMapper.map(products);

        // 4. 필터링 : 아예 상관없는 제품 제거
        List<ProductSupply> filtered =
                productFilterPolicy.filter(supplies, need);

        // 5. 점수 계산 각 제품이 Need에 얼만큼 부합하는지 계산
        List<ProductScore> scores =
                productScoreCalculator.calculate(need, filtered);

        // 6. 정렬 + 변환
        return toRecommended(products, scores);
    }

    private Map<IngredientGroup, Double> loadNeed(Long resultId) {
        return skinResultGroupScoreRepository.findBySkinResultId(resultId).stream()
                .collect(Collectors.toMap(
                        SkinResultGroupScore::getIngredientGroup,
                        SkinResultGroupScore::getScore)
                );
    }

    private List<RecommendedProduct> toRecommended(
            List<Product> products,
            List<ProductScore> scores
    ) {
        Map<Long, Product> productMap = products.stream()
                .collect(Collectors.toMap(
                        Product::getId,
                        p -> p
                ));

        // 카테고리별 그룹핑
        Map<ProductCategory, List<ProductScore>> grouped = scores.stream()
                .collect(Collectors.groupingBy(s ->
                        productMap.get(s.getProductId()).getCategory()
                ));

        // 카테고리별 5개로
        int perCategoryLimit = 5;

        List<RecommendedProduct> merged = new ArrayList<>();

        for (Map.Entry<ProductCategory, List<ProductScore>> entry : grouped.entrySet()) {

            List<RecommendedProduct> topByCategory = entry.getValue().stream()
                    .sorted(Comparator.comparingDouble(ProductScore::getScore).reversed())
                    .limit(perCategoryLimit)
                    .map(score -> new RecommendedProduct(
                            productMap.get(score.getProductId()),
                            score.getScore()
                    ))
                    .toList();

            merged.addAll(topByCategory);
        }

        return merged.stream()
                .sorted(Comparator.comparingDouble(RecommendedProduct::getScore).reversed())
                .toList();
    }
}
