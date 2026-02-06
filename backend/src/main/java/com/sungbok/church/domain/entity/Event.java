package com.sungbok.church.domain.entity;

import com.sungbok.church.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * 행사 일정 Entity
 */
@Entity
@Table(name = "event", indexes = {
    @Index(name = "idx_event_date", columnList = "startDate ASC"),
    @Index(name = "idx_event_published", columnList = "isPublished, startDate ASC")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "id", callSuper = false)
@ToString
public class Event extends BaseEntity {

    @Column(nullable = false, length = 200)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(length = 50)
    private String category;

    @Column(length = 100)
    private String organizer;

    @Column(length = 500)
    private String posterImageUrl;

    @Column(length = 100)
    private String location;

    @Column(nullable = false)
    private LocalDateTime startDate;

    @Column(nullable = false)
    private LocalDateTime endDate;

    @Column(nullable = false)
    @Builder.Default
    private Boolean registrationRequired = false;

    private Integer maxParticipants;

    @Column(nullable = false)
    @Builder.Default
    private Integer currentParticipants = 0;

    @Column(nullable = false)
    @Builder.Default
    private Integer viewCount = 0;

    @Column(nullable = false)
    @Builder.Default
    private Boolean isPublished = true;
}
