package com.swyp3.skin.domain.routine.service;

import com.swyp3.skin.api.v1.routine.dto.request.SaveRoutineRequest;
import com.swyp3.skin.api.v1.routine.dto.response.RoutineRecommendationResponse;
import com.swyp3.skin.api.v1.routine.dto.response.RoutineRecommendedProductResponse;
import com.swyp3.skin.api.v1.routine.dto.response.RoutineSectionResponse;
import com.swyp3.skin.api.v1.routine.dto.response.SaveRoutineResponse;
import com.swyp3.skin.domain.product.domain.entity.Product;
import com.swyp3.skin.domain.product.service.ProductService;
import com.swyp3.skin.domain.routine.domain.entity.Routine;
import com.swyp3.skin.domain.routine.domain.entity.RoutineGroup;
import com.swyp3.skin.domain.routine.domain.entity.RoutineProduct;
import com.swyp3.skin.domain.routine.domain.enums.RoutineStepCategory;
import com.swyp3.skin.domain.routine.domain.enums.RoutineType;
import com.swyp3.skin.domain.routine.dto.RoutinePreviewCacheValue;
import com.swyp3.skin.domain.routine.exception.RoutineErrorCode;
import com.swyp3.skin.domain.routine.exception.RoutineException;
import com.swyp3.skin.domain.routine.repository.RoutineGroupRepository;
import com.swyp3.skin.domain.routine.repository.RoutineProductRepository;
import com.swyp3.skin.domain.routine.repository.RoutineRepository;
import com.swyp3.skin.domain.skinresult.domain.entity.SkinResult;
import com.swyp3.skin.domain.skinresult.service.SkinResultService;
import com.swyp3.skin.domain.user.domain.entity.User;
import com.swyp3.skin.domain.user.service.UserService;
import com.swyp3.skin.global.auth.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@RequiredArgsConstructor
@Service
public class RoutineCommandService {

    private final RoutinePreviewCacheService routinePreviewCacheService;
    private final UserService userService;
    private final SkinResultService skinResultService;
    private final ProductService productService;
    private final RoutineGroupRepository routineGroupRepository;
    private final RoutineRepository routineRepository;
    private final RoutineProductRepository routineProductRepository;

    @Transactional
    public void deleteRoutine(Long routineGroupId, Long userId) {
        routineGroupRepository.findByIdAndUser_Id(routineGroupId, userId)
                .orElseThrow(() -> new RoutineException(RoutineErrorCode.ROUTINE_NOT_FOUND));

        List<Routine> routines = routineRepository.findByRoutineGroup_Id(routineGroupId);
        for (Routine routine : routines) {
            routineProductRepository.deleteByRoutine_Id(routine.getId());
            routineRepository.delete(routine);
        }

        routineGroupRepository.deleteById(routineGroupId);
    }

    @Transactional
    public SaveRoutineResponse save(CustomUserDetails userDetails, SaveRoutineRequest request) {
        RoutineRecommendationResponse response = resolveRoutineData(userDetails, request);

        routineGroupRepository.findByUser_IdAndSkinResult_Id(userDetails.userId(), response.skinResultId())
                .ifPresent(routineGroup -> {
                    throw new RoutineException(RoutineErrorCode.DUPLICATE_ROUTINE_FOR_SKIN_RESULT);
                });

        User user = userService.findById(userDetails.userId());
        SkinResult skinResult = skinResultService.getSkinResultById(response.skinResultId(), userDetails.userId());


        RoutineGroup routineGroup = routineGroupRepository.save(RoutineGroup.of(
                user,
                skinResult,
                request.title(),
                response.skinType(),
                response.subtitle(),
                response.routineSummary()
        ));

        saveSection(routineGroup, response.amRoutine());
        saveSection(routineGroup, response.pmRoutine());

        return new SaveRoutineResponse(
                routineGroup.getId(),
                routineGroup.getTitle()
        );
    }

    private RoutineRecommendationResponse resolveRoutineData(CustomUserDetails userDetails, SaveRoutineRequest request) {
        RoutinePreviewCacheValue cached = routinePreviewCacheService.consume(request.previewToken());
        if (cached == null || !cached.userId().equals(userDetails.userId())) {
            throw new RoutineException(RoutineErrorCode.PREVIEW_TOKEN_EXPIRED);
        }

        RoutineRecommendationResponse response = cached.response();
        validatePreviewResponse(response);
        return response;
    }

    private void validatePreviewResponse(RoutineRecommendationResponse response) {
        if (response == null || response.skinResultId() == null) {
            throw new RoutineException(RoutineErrorCode.PREVIEW_RESPONSE_NOT_FOUND);
        }

        if (response.amRoutine() == null || response.pmRoutine() == null) {
            throw new RoutineException(RoutineErrorCode.PREVIEW_ROUTINE_EMPTY);
        }

        if (response.amRoutine().routineType() != RoutineType.AM
                || response.pmRoutine().routineType() != RoutineType.PM) {
            throw new RoutineException(RoutineErrorCode.PREVIEW_SECTION_INVALID);
        }

        validateSection(response.amRoutine());
        validateSection(response.pmRoutine());
    }

    private void validateSection(RoutineSectionResponse section) {
        if (section.routineType() == null || section.products() == null || section.products().isEmpty()) {
            throw new RoutineException(RoutineErrorCode.PREVIEW_SECTION_INVALID);
        }

        if (section.routineType() != RoutineType.AM && section.routineType() != RoutineType.PM) {
            throw new RoutineException(RoutineErrorCode.PREVIEW_SECTION_INVALID);
        }

        Set<RoutineStepCategory> stepCategories = new HashSet<>();
        for (RoutineRecommendedProductResponse productResponse : section.products()) {
            if (productResponse == null
                    || productResponse.productId() == null
                    || productResponse.routineStepCategory() == null
                    || !stepCategories.add(productResponse.routineStepCategory())) {
                throw new RoutineException(RoutineErrorCode.PREVIEW_PRODUCT_INVALID);
            }
        }
    }

    private void saveSection(RoutineGroup routineGroup, RoutineSectionResponse section) {
        Routine routine = routineRepository.save(Routine.of(
                routineGroup,
                section.routineType()
        ));

        List<RoutineProduct> routineProducts = section.products().stream()
                .map(productResponse -> toRoutineProduct(routine, productResponse))
                .toList();

        routineProductRepository.saveAll(routineProducts);
    }

    private RoutineProduct toRoutineProduct(
            Routine routine,
            RoutineRecommendedProductResponse productResponse
    ) {
        Product product = productService.getById(productResponse.productId());
        RoutineStepCategory routineStepCategory = productResponse.routineStepCategory();
        return RoutineProduct.of(
                routine,
                product,
                routineStepCategory
        );
    }
}
