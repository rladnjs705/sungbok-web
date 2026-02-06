package com.sungbok.church.domain.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 공지사항 카테고리
 */
@Getter
@RequiredArgsConstructor
public enum NoticeCategory {
    NEWS("새소식"),
    EVENT("행사안내"),
    ANNOUNCEMENT("공지사항"),
    BULLETIN("주보");

    private final String description;
}
