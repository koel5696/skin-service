package com.swyp3.skin.domain.skinresult.repository;

import com.swyp3.skin.domain.skinresult.domain.entity.SkinResultGroupScore;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SkinResultGroupScoreRepository extends JpaRepository<SkinResultGroupScore, Long> {
    List<SkinResultGroupScore> findBySkinResultId(Long skinResultId);

    List<SkinResultGroupScore> findBySkinResultIdOrderByPriorityAsc(Long skinResultId);

    List<SkinResultGroupScore> findTop2BySkinResultIdOrderByPriorityAsc(Long resultId);
}
