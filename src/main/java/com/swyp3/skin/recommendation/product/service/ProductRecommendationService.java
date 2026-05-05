package com.swyp3.skin.recommendation.product.service;

import com.swyp3.skin.domain.common.enums.IngredientGroup;
import com.swyp3.skin.domain.product.domain.entity.Product;
import com.swyp3.skin.domain.product.domain.enums.ProductCategory;
import com.swyp3.skin.domain.product.domain.enums.ProductUsageTime;
import com.swyp3.skin.domain.product.repository.ProductRepository;
import com.swyp3.skin.domain.skinresult.domain.entity.SkinResultGroupScore;
import com.swyp3.skin.domain.skinresult.repository.SkinResultGroupScoreRepository;
import com.swyp3.skin.recommendation.product.calculator.ProductScoreCalculator;
import com.swyp3.skin.recommendation.product.dto.ProductScore;
import com.swyp3.skin.recommendation.product.dto.ProductSupply;
import com.swyp3.skin.recommendation.product.dto.RecommendedProduct;
import com.swyp3.skin.recommendation.product.mapper.ProductVectorMapper;
import com.swyp3.skin.recommendation.product.policy.ProductFilterPolicy;
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
    private final static int PER_CATEGORY_LIMIT = 5;

    private final ProductRepository productRepository;
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

        List<RecommendedProduct> merged = new ArrayList<>();

        for (Map.Entry<ProductCategory, List<ProductScore>> entry : grouped.entrySet()) {
            merged.addAll(selectTopByCategory(entry.getValue(), productMap));
        }

        return merged.stream()
                .sorted(Comparator.comparingDouble(RecommendedProduct::getScore).reversed())
                .toList();
    }

    //여기서 상품 목록을 띄울 때 기존처럼 그냥 리미트 5개 되는지 확인!!
    //여기서 상품 목록을 띄울 때 기존처럼 그냥 리미트 5개 되는지 확인!!
    //여기서 상품 목록을 띄울 때 기존처럼 그냥 리미트 5개 되는지 확인!!
    //리스트에는 ampm 구분이 없음. 넣을때만 구분하는거지
    private List<RecommendedProduct> selectTopByCategory(
            List<ProductScore> categoryScores,
            Map<Long, Product> productMap
    ) {
        List<ProductScore> sorted = categoryScores.stream()
                .sorted(Comparator.comparingDouble(ProductScore::getScore).reversed())
                .toList();

        List<RecommendedProduct> selected = new ArrayList<>();
        int amCount = 0;
        int pmCount = 0;

        for (ProductScore score : sorted) {
            Product product = productMap.get(score.getProductId());
            ProductUsageTime usageTime = product.getProductUsageTime(); // 정책 상은 사용시간이 null 일 수 없음. db 반영예정.
            boolean shouldInclude = false;

            switch (usageTime) {
                case AM -> {
                    if (amCount < PER_CATEGORY_LIMIT) {
                        amCount++;
                        shouldInclude = true;
                    }

                }
                case PM -> {
                    if (pmCount < PER_CATEGORY_LIMIT) {
                        pmCount++;
                        shouldInclude = true;
                    }
                }
                case BOTH -> {
                    if (amCount < PER_CATEGORY_LIMIT) amCount++;
                    if (pmCount < PER_CATEGORY_LIMIT) pmCount++;
                    shouldInclude = true; // 둘 중 하나라도 슬롯 있으면 포함
                }
            }

            if (shouldInclude) {
                selected.add(new RecommendedProduct(product, score.getScore()));
            }

            if (amCount == PER_CATEGORY_LIMIT && pmCount == PER_CATEGORY_LIMIT) {
                break;
            }

            if(product.getCategory() == ProductCategory.SUN_CARE && amCount == 5) {
                break; // 선크림은 am루틴에만 포함. 5개가 채워지면 즉시 종료.
            }
        }

        return selected;
    }
}
