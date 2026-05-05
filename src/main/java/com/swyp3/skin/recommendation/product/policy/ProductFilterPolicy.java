package com.swyp3.skin.recommendation.product.policy;

import com.swyp3.skin.domain.common.enums.IngredientGroup;
import com.swyp3.skin.domain.product.domain.enums.ProductCategory;
import com.swyp3.skin.recommendation.product.dto.ProductSupply;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class ProductFilterPolicy {

    public List<ProductSupply> filter(
            List<ProductSupply> supplies,
            Map<IngredientGroup, Double> need) {

        Set<IngredientGroup> top2Groups = need.entrySet().stream()
                .sorted(Map.Entry.<IngredientGroup, Double>comparingByValue().reversed())
                .limit(2)
                .map(Map.Entry::getKey)
                .collect(Collectors.toSet());

        //선크림은 애초에 탑4까지 확인하여 상품 살림.
        Set<IngredientGroup> top4Groups = need.entrySet().stream()
                .sorted(Map.Entry.<IngredientGroup, Double>comparingByValue().reversed())
                .limit(4)
                .map(Map.Entry::getKey)
                .collect(Collectors.toSet());

        return supplies.stream()
                .filter(supply -> {
                    Set<IngredientGroup> threshold =
                            supply.getProductCategory() == ProductCategory.SUN_CARE
                                    ? top4Groups
                                    : top2Groups;
                    return supply.getSupply().keySet().stream()
                            .anyMatch(threshold::contains);
                })
                .collect(Collectors.toList());
    }
}
