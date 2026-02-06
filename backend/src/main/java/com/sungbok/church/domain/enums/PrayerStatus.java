package com.sungbok.church.domain.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 기도요청 상태
 */
@Getter
@RequiredArgsConstructor
public enum PrayerStatus {
    PENDING("대기"),
    APPROVED("승인"),
    PRAYING("기도 중"),
    ANSWERED("응답됨");

    private final String description;
}
