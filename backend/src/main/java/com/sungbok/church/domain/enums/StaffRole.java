package com.sungbok.church.domain.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 섬기는 이들 직분
 */
@Getter
@RequiredArgsConstructor
public enum StaffRole {
    ELDER("장로"),
    EXHORTER("권사"),
    DEACON("집사"),
    DEACONESS("안수집사");

    private final String description;
}
