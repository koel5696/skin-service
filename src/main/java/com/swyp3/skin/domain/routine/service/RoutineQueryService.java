package com.swyp3.skin.domain.routine.service;

import com.swyp3.skin.api.v1.routine.dto.response.RoutineDetailResponse;
import com.swyp3.skin.api.v1.routine.dto.response.RoutineListResponse;
import com.swyp3.skin.api.v1.routine.dto.response.RoutineRecommendedProductResponse;
import com.swyp3.skin.api.v1.routine.dto.response.RoutineSectionResponse;
import com.swyp3.skin.api.v1.routine.dto.response.RoutineSummaryResponse;
import com.swyp3.skin.domain.routine.domain.entity.Routine;
import com.swyp3.skin.domain.routine.domain.entity.RoutineGroup;
import com.swyp3.skin.domain.routine.domain.entity.RoutineProduct;
import com.swyp3.skin.domain.routine.domain.enums.RoutineType;
import com.swyp3.skin.domain.routine.exception.RoutineErrorCode;
import com.swyp3.skin.domain.routine.exception.RoutineException;
import com.swyp3.skin.domain.routine.repository.RoutineGroupRepository;
import com.swyp3.skin.domain.routine.repository.RoutineProductRepository;
import com.swyp3.skin.domain.routine.repository.RoutineRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;

import static com.swyp3.skin.global.timeUtil.TimeUtils.formatToKstDate;
import static com.swyp3.skin.global.timeUtil.TimeUtils.formatToKstDateTime;

@Service
@RequiredArgsConstructor
public class RoutineQueryService {

    private final RoutineGroupRepository routineGroupRepository;
    private final RoutineRepository routineRepository;
    private final RoutineProductRepository routineProductRepository;

    public RoutineListResponse inquiryRoutineGroups(Long userId, Long cursor, int size) {
        PageRequest pageable = PageRequest.of(0, size + 1);

        List<RoutineGroup> routineGroups = (cursor == null)
                ? routineGroupRepository.findByUser_IdOrderByIdDesc(userId, pageable)
                : routineGroupRepository.findByUser_IdAndIdLessThanOrderByIdDesc(userId, cursor, pageable);

        boolean hasNext = routineGroups.size() > size;
        if (hasNext) {
            routineGroups = routineGroups.subList(0, size);
        }
        List<RoutineSummaryResponse> routines = routineGroups.stream()
                .map(routineGroup -> new RoutineSummaryResponse(
                        routineGroup.getId(),
                        routineGroup.getTitle(),
                        formatToKstDate(routineGroup.getCreatedAt())
                ))
                .toList();

        return RoutineListResponse.from(routines, hasNext);
    }

    public RoutineDetailResponse inquiryDetailRoutineGroup(Long userId, Long routineGroupId) {
        RoutineGroup routineGroup = routineGroupRepository.findByIdAndUser_Id(routineGroupId, userId).orElseThrow(
                () -> new RoutineException(RoutineErrorCode.ROUTINE_NOT_FOUND));

        List<Routine> routines = routineRepository.findByRoutineGroup_Id(routineGroup.getId());
        RoutineSectionResponse amRoutine = null;
        RoutineSectionResponse pmRoutine = null;

        for(Routine routine : routines) {
            List<RoutineProduct> products = routineProductRepository.findByRoutine_Id(routine.getId());
            List<RoutineRecommendedProductResponse> routineProducts = products.stream()
                    .sorted(Comparator.comparing(routineProduct -> routineProduct.getRoutineStepCategory().getOrder()))
                    .map(this::toRoutineRecommendedProductResponse)
                    .toList();

            RoutineSectionResponse routineSectionResponse = new RoutineSectionResponse(
                    routine.getRoutineType(),
                    routineProducts
            );

            if (routine.getRoutineType() == RoutineType.AM) {
                amRoutine = routineSectionResponse;
            }
            if (routine.getRoutineType() == RoutineType.PM) {
                pmRoutine = routineSectionResponse;
            }
        }

        return new RoutineDetailResponse(
                routineGroup.getId(),
                routineGroup.getSkinResult().getId(),
                routineGroup.getTitle(),
                routineGroup.getSkinType(),
                routineGroup.getSubtitle(),
                routineGroup.getSummary(),
                amRoutine,
                pmRoutine,
                formatToKstDateTime(routineGroup.getCreatedAt())
        );
    }

    private RoutineRecommendedProductResponse toRoutineRecommendedProductResponse(RoutineProduct routineProduct) {
        return new RoutineRecommendedProductResponse(
                routineProduct.getProduct().getId(),
                routineProduct.getProduct().getName(),
                routineProduct.getProduct().getPrice(),
                routineProduct.getProduct().getCategory(),
                routineProduct.getProduct().getImageUrl(),
                routineProduct.getRoutineStepCategory()
        );
    }
}
