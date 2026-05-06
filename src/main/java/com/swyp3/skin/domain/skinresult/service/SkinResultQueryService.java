package com.swyp3.skin.domain.skinresult.service;

import com.swyp3.skin.api.v1.skintest.dto.response.SkinResultListResponse;
import com.swyp3.skin.api.v1.user.dto.response.mypage.SkinResultSummary;
import com.swyp3.skin.domain.skinresult.domain.entity.SkinResult;
import com.swyp3.skin.domain.skinresult.repository.SkinResultRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SkinResultQueryService {

    private final SkinResultRepository skinResultRepository;

    public SkinResultListResponse getSkinResults(Long userId, Long cursor, int size) {
        PageRequest pageable = PageRequest.of(0, size + 1);

        List<SkinResult> skinResults = (cursor == null)
                ? skinResultRepository.findByUser_IdOrderByIdDesc(userId, pageable)
                : skinResultRepository.findByUser_IdAndIdLessThanOrderByIdDesc(userId, cursor, pageable);

        boolean hasNext = skinResults.size() > size;
        if (hasNext) {
            skinResults = skinResults.subList(0, size);
        }

        List<SkinResultSummary> summaries = skinResults.stream()
                .map(skinResult -> SkinResultSummary.from(skinResult, skinResult.getTypeName()))
                .toList();

        return SkinResultListResponse.from(summaries, hasNext);
    }
}
