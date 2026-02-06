package com.sungbok.church.domain.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * YouTube 라이브 상태
 */
@Getter
@RequiredArgsConstructor
public enum LiveStatus {
    SCHEDULED("예정"),
    UPCOMING("다가오는"),
    LIVE("방송중"),
    COMPLETED("완료"),
    ENDED("종료");

    private final String description;
}
