package com.sungbok.church.domain.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 예배 유형
 */
@Getter
@RequiredArgsConstructor
public enum WorshipType {
    SUNDAY("주일예배"),
    WEDNESDAY("수요예배"),
    DAWN("새벽예배"),
    SPECIAL("특별예배");

    private final String description;
}
