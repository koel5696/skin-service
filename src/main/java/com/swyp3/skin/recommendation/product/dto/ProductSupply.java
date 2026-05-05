package com.swyp3.skin.recommendation.product.dto;

import com.swyp3.skin.domain.common.enums.IngredientGroup;
import com.swyp3.skin.domain.product.domain.enums.ProductCategory;
import lombok.Getter;

import java.util.Map;

@Getter
public class ProductSupply {

    private final Long productId;
    private final ProductCategory productCategory;
    private final Map<IngredientGroup, Double> supply;

    public ProductSupply(Long productId, ProductCategory productCategory, Map<IngredientGroup, Double> supply) {
        this.productId = productId;
        this.productCategory = productCategory;
        this.supply = supply;
    }
}
