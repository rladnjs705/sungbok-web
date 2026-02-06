package com.sungbok.church.domain.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 선교 유형
 */
@Getter
@RequiredArgsConstructor
public enum MissionType {
    DOMESTIC("국내선교"),
    OVERSEAS("해외선교"),
    SOCIAL_SERVICE("사회봉사");

    private final String description;
}
