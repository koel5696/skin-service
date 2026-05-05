package com.swyp3.skin.domain.skinresult.service;

import com.swyp3.skin.domain.skinresult.domain.entity.SkinResult;
import com.swyp3.skin.domain.skinresult.domain.exception.SkinResultErrorCode;
import com.swyp3.skin.domain.skinresult.domain.exception.SkinResultException;
import com.swyp3.skin.domain.skinresult.repository.SkinResultRepository;
import com.swyp3.skin.domain.skintest.dto.SkinPreviewCacheValue;
import com.swyp3.skin.domain.user.domain.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SkinResultService {

    private final SkinResultRepository skinResultRepository;

    @Transactional
    public SkinResult saveFromResolvedData(User user, SkinPreviewCacheValue cached) {
        SkinResult skinResult = SkinResult.create(
                user, cached.skinInput().getSkinType() , cached.typeName(), cached.skinInput().getConcerns(),LocalDateTime.now()
        );
        return skinResultRepository.save(skinResult);
    }

    public SkinResult getLatestByUserId(Long userId) {
        return skinResultRepository.findTopByUser_IdOrderByCreatedAtDesc(userId)
                .orElseThrow(() -> new SkinResultException(SkinResultErrorCode.SKIN_RESULT_NOT_YET));
    }

    public List<SkinResult> getTop3ByUserId(Long userId) {
        return skinResultRepository.findTop3ByUser_IdOrderByCreatedAtDesc(userId);
    }

    public SkinResult getSkinResultById(Long skinResultId,Long userId) {
        return skinResultRepository.findByIdAndUser_id(skinResultId,userId)
                .orElseThrow(() -> new SkinResultException(SkinResultErrorCode.SKIN_RESULT_NOT_FOUND));
    }


}
