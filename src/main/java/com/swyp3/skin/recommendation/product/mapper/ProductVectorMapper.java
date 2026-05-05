package com.swyp3.skin.recommendation.product.mapper;

import com.swyp3.skin.domain.common.enums.IngredientGroup;
import com.swyp3.skin.domain.product.domain.entity.Product;
import com.swyp3.skin.domain.product.domain.entity.ProductGroupScore;
import com.swyp3.skin.domain.product.repository.ProductGroupScoreRepository;
import com.swyp3.skin.recommendation.product.dto.ProductSupply;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ProductVectorMapper {

    private final ProductGroupScoreRepository productGroupScoreRepository;

    public List<ProductSupply> map(List<Product> products) {
        List<Long> productIds = products.stream()
                .map(Product::getId)
                .toList();

        List<ProductGroupScore> scores =
                productGroupScoreRepository.findByProductIdIn(productIds);
        Map<Long, Product> productMap = products.stream()
                .collect(Collectors.toMap(Product::getId, p -> p));

        Map<Long, List<ProductGroupScore>> grouped = scores.stream()
                .collect(Collectors.groupingBy(
                        s -> s.getProduct().getId()
                ));

        List<ProductSupply> result = new ArrayList<>();
        for (Map.Entry<Long, List<ProductGroupScore>> entry : grouped.entrySet()) {
            Long productId = entry.getKey();
            Product product = productMap.get(productId);

            Map<IngredientGroup, Double> supply = entry.getValue().stream()
                    .collect(Collectors.toMap(
                            ProductGroupScore::getIngredientGroup,
                            ProductGroupScore::getScore
                    ));

            result.add(new ProductSupply(productId, product.getCategory(), supply));
        }
        return result;
    }

}
