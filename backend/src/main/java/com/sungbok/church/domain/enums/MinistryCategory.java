package com.sungbok.church.domain.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 교육/양육 부서 카테고리
 */
@Getter
@RequiredArgsConstructor
public enum MinistryCategory {
    SUNDAY_SCHOOL("주일학교"),
    YOUTH("청년부"),
    ADULT("장년부"),
    SENIOR("경로부"),
    NEWCOMER("새가족"),
    BIBLE_STUDY("성경공부"),
    DISCIPLE_TRAINING("제자훈련");

    private final String description;
}
