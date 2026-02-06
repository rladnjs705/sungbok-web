package com.sungbok.church.domain.entity;

import com.sungbok.church.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

/**
 * 공지사항 첨부파일 Entity
 */
@Entity
@Table(name = "notice_attachment", indexes = {
    @Index(name = "idx_attachment_notice", columnList = "notice_id")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "id", callSuper = false)
@ToString(exclude = {"notice"})
public class NoticeAttachment extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "notice_id", nullable = false)
    private Notice notice;

    @Column(nullable = false, length = 255)
    private String fileName;

    @Column(nullable = false, length = 500)
    private String fileUrl;

    @Column(nullable = false)
    private Long fileSize;

    @Column(nullable = false, length = 50)
    private String fileType;
}
