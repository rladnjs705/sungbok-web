package com.sungbok.church.controller;

import com.sungbok.church.domain.entity.Notice;
import com.sungbok.church.domain.entity.NoticeAttachment;
import com.sungbok.church.domain.enums.NoticeCategory;
import com.sungbok.church.dto.request.NoticeRequest;
import com.sungbok.church.dto.response.AttachmentResponse;
import com.sungbok.church.dto.response.NoticeResponse;
import com.sungbok.church.service.NoticeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Notice Controller
 * 공지사항 관리 REST API
 */
@Tag(name = "공지사항", description = "공지사항 관리 API")
@RestController
@RequestMapping("/api/notices")
@RequiredArgsConstructor
public class NoticeController {

    private final NoticeService noticeService;

    /**
     * 공지사항 목록 조회 (페이징)
     */
    @Operation(summary = "공지사항 목록 조회", description = "전체 공지사항 목록을 페이징하여 조회합니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "조회 성공")
    })
    @GetMapping
    public ResponseEntity<Page<NoticeResponse>> getNotices(
            @Parameter(description = "페이징 정보 (기본 20개, 최신순)")
            @PageableDefault(size = 20, sort = "publishedAt", direction = Sort.Direction.DESC) Pageable pageable) {
        Page<NoticeResponse> notices = noticeService.getNotices(pageable)
            .map(NoticeResponse::from);
        return ResponseEntity.ok(notices);
    }

    /**
     * 카테고리별 공지사항 조회
     */
    @Operation(summary = "카테고리별 공지사항 조회", description = "특정 카테고리의 공지사항을 조회합니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "조회 성공")
    })
    @GetMapping("/category/{category}")
    public ResponseEntity<Page<NoticeResponse>> getNoticesByCategory(
            @Parameter(description = "공지사항 카테고리 (GENERAL, WORSHIP, EVENT, EDUCATION, MISSION)")
            @PathVariable NoticeCategory category,
            @Parameter(description = "페이징 정보")
            @PageableDefault(size = 20) Pageable pageable) {
        Page<NoticeResponse> notices = noticeService.getNoticesByCategory(category, pageable)
            .map(NoticeResponse::from);
        return ResponseEntity.ok(notices);
    }

    /**
     * 상단 고정 공지사항 조회
     */
    @Operation(summary = "상단 고정 공지사항 조회", description = "상단에 고정된 중요 공지사항을 조회합니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "조회 성공")
    })
    @GetMapping("/pinned")
    public ResponseEntity<List<NoticeResponse>> getPinnedNotices() {
        List<NoticeResponse> notices = noticeService.getPinnedNotices().stream()
            .map(NoticeResponse::from)
            .collect(Collectors.toList());
        return ResponseEntity.ok(notices);
    }

    /**
     * 공지사항 ID로 조회 (첨부 파일 포함)
     */
    @Operation(summary = "공지사항 상세 조회", description = "공지사항 ID로 상세 정보를 조회합니다. 조회수가 증가하며 첨부 파일 목록이 포함됩니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "조회 성공"),
        @ApiResponse(responseCode = "404", description = "공지사항을 찾을 수 없음", content = @Content)
    })
    @GetMapping("/{id}")
    public ResponseEntity<NoticeResponse> getNoticeById(
            @Parameter(description = "공지사항 ID") @PathVariable Long id) {
        Notice notice = noticeService.getNoticeById(id);
        List<NoticeAttachment> attachments = noticeService.getAttachments(id);
        return ResponseEntity.ok(NoticeResponse.from(notice, attachments));
    }

    /**
     * 공지사항 첨부 파일 목록 조회
     */
    @Operation(summary = "첨부 파일 목록 조회", description = "특정 공지사항의 첨부 파일 목록을 조회합니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "조회 성공")
    })
    @GetMapping("/{id}/attachments")
    public ResponseEntity<List<AttachmentResponse>> getAttachments(
            @Parameter(description = "공지사항 ID") @PathVariable Long id) {
        List<AttachmentResponse> attachments = noticeService.getAttachments(id).stream()
            .map(AttachmentResponse::from)
            .collect(Collectors.toList());
        return ResponseEntity.ok(attachments);
    }

    /**
     * 관련 소식 조회 (같은 카테고리, 랜덤 4개)
     */
    @Operation(summary = "관련 소식 조회", description = "현재 공지사항과 같은 카테고리의 다른 소식을 랜덤으로 4개 조회합니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "조회 성공")
    })
    @GetMapping("/{id}/related")
    public ResponseEntity<List<NoticeResponse>> getRelatedNotices(
            @Parameter(description = "공지사항 ID") @PathVariable Long id) {
        Notice notice = noticeService.getNoticeWithAttachments(id);
        List<NoticeResponse> related = noticeService.getRelatedNotices(id, notice.getCategory()).stream()
            .map(NoticeResponse::from)
            .collect(Collectors.toList());
        return ResponseEntity.ok(related);
    }

    /**
     * 제목으로 검색
     */
    @Operation(summary = "제목으로 공지사항 검색", description = "공지사항 제목에서 키워드를 검색합니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "검색 성공")
    })
    @GetMapping("/search/title")
    public ResponseEntity<Page<NoticeResponse>> searchByTitle(
            @Parameter(description = "검색 키워드") @RequestParam String keyword,
            @Parameter(description = "페이징 정보") @PageableDefault(size = 20) Pageable pageable) {
        Page<NoticeResponse> notices = noticeService.searchByTitle(keyword, pageable)
            .map(NoticeResponse::from);
        return ResponseEntity.ok(notices);
    }

    /**
     * 제목 또는 내용으로 검색
     */
    @Operation(summary = "통합 검색", description = "공지사항 제목 또는 내용에서 키워드를 검색합니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "검색 성공")
    })
    @GetMapping("/search")
    public ResponseEntity<Page<NoticeResponse>> searchByKeyword(
            @Parameter(description = "검색 키워드") @RequestParam String keyword,
            @Parameter(description = "페이징 정보") @PageableDefault(size = 20) Pageable pageable) {
        Page<NoticeResponse> notices = noticeService.searchByKeyword(keyword, pageable)
            .map(NoticeResponse::from);
        return ResponseEntity.ok(notices);
    }

    /**
     * 공지사항 생성 (JSON only)
     */
    @Operation(summary = "공지사항 생성", description = "새로운 공지사항을 생성합니다. (관리자 권한 필요)")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "생성 성공"),
        @ApiResponse(responseCode = "400", description = "잘못된 요청", content = @Content),
        @ApiResponse(responseCode = "401", description = "인증 실패", content = @Content)
    })
    @PostMapping
    public ResponseEntity<NoticeResponse> createNotice(
            @Parameter(description = "공지사항 생성 정보") @Valid @RequestBody NoticeRequest request) {
        Notice notice = toEntity(request);
        Notice created = noticeService.createNotice(notice);
        return ResponseEntity.status(HttpStatus.CREATED).body(NoticeResponse.from(created));
    }

    /**
     * 공지사항 생성 + 첨부 파일 업로드
     */
    @Operation(summary = "공지사항 생성 (파일 포함)", description = "새로운 공지사항을 생성하고 파일을 함께 업로드합니다. (관리자 권한 필요)")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "생성 성공"),
        @ApiResponse(responseCode = "400", description = "잘못된 요청", content = @Content),
        @ApiResponse(responseCode = "401", description = "인증 실패", content = @Content)
    })
    @PostMapping(value = "/with-files", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<NoticeResponse> createNoticeWithFiles(
            @Parameter(description = "공지사항 생성 정보 (JSON)") @RequestPart("notice") @Valid NoticeRequest request,
            @Parameter(description = "첨부 파일들") @RequestPart(value = "files", required = false) List<MultipartFile> files) {
        Notice notice = toEntity(request);
        Notice created = noticeService.createNoticeWithAttachments(notice, files);
        return ResponseEntity.status(HttpStatus.CREATED).body(NoticeResponse.from(created));
    }

    /**
     * 공지사항에 파일 추가 업로드
     */
    @Operation(summary = "파일 추가 업로드", description = "기존 공지사항에 파일을 추가합니다. (관리자 권한 필요)")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "업로드 성공"),
        @ApiResponse(responseCode = "404", description = "공지사항을 찾을 수 없음", content = @Content)
    })
    @PostMapping(value = "/{id}/attachments", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<List<AttachmentResponse>> uploadAttachments(
            @Parameter(description = "공지사항 ID") @PathVariable Long id,
            @Parameter(description = "첨부 파일들") @RequestPart("files") List<MultipartFile> files) {
        Notice notice = noticeService.getNoticeWithAttachments(id);
        noticeService.uploadAttachments(notice, files);
        
        List<AttachmentResponse> attachments = noticeService.getAttachments(id).stream()
            .map(AttachmentResponse::from)
            .collect(Collectors.toList());
        return ResponseEntity.ok(attachments);
    }

    /**
     * 공지사항 수정
     */
    @Operation(summary = "공지사항 수정", description = "기존 공지사항을 수정합니다. (관리자 권한 필요)")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "수정 성공"),
        @ApiResponse(responseCode = "400", description = "잘못된 요청", content = @Content),
        @ApiResponse(responseCode = "401", description = "인증 실패", content = @Content),
        @ApiResponse(responseCode = "404", description = "공지사항을 찾을 수 없음", content = @Content)
    })
    @PutMapping("/{id}")
    public ResponseEntity<NoticeResponse> updateNotice(
            @Parameter(description = "공지사항 ID") @PathVariable Long id,
            @Parameter(description = "공지사항 수정 정보") @Valid @RequestBody NoticeRequest request) {
        Notice notice = toEntity(request);
        Notice updated = noticeService.updateNotice(id, notice);
        return ResponseEntity.ok(NoticeResponse.from(updated));
    }

    /**
     * 공지사항 삭제
     */
    @Operation(summary = "공지사항 삭제", description = "공지사항을 삭제합니다. (관리자 권한 필요)")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "삭제 성공"),
        @ApiResponse(responseCode = "401", description = "인증 실패", content = @Content),
        @ApiResponse(responseCode = "404", description = "공지사항을 찾을 수 없음", content = @Content)
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteNotice(
            @Parameter(description = "공지사항 ID") @PathVariable Long id) {
        noticeService.deleteNotice(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * 통계: 카테고리별 공지사항 개수
     */
    @Operation(summary = "카테고리별 공지사항 개수", description = "특정 카테고리의 공지사항 총 개수를 조회합니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "조회 성공")
    })
    @GetMapping("/stats/category/{category}")
    public ResponseEntity<Long> getNoticeCountByCategory(
            @Parameter(description = "공지사항 카테고리") @PathVariable NoticeCategory category) {
        return ResponseEntity.ok(noticeService.getNoticeCountByCategory(category));
    }

    /**
     * Request DTO -> Entity 변환
     */
    private Notice toEntity(NoticeRequest request) {
        return Notice.builder()
            .category(request.getCategory())
            .title(request.getTitle())
            .content(request.getContent())
            .author(request.getAuthor())
            .isPinned(request.getIsPinned())
            .publishedAt(request.getPublishedAt())
            .build();
    }
}
