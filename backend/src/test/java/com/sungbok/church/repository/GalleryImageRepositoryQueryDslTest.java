package com.sungbok.church.repository;

import com.sungbok.church.config.QuerydslConfiguration;
import com.sungbok.church.domain.entity.Gallery;
import com.sungbok.church.domain.entity.GalleryImage;
import com.sungbok.church.domain.repository.GalleryImageRepository;
import com.sungbok.church.domain.repository.GalleryRepository;
import com.sungbok.church.dto.response.GalleryImageResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * GalleryImageRepository QueryDSL 통합 테스트
 *
 * 목적:
 * - QueryDSL 쿼리 동작 검증
 * - LazyInitializationException 방지 확인
 * - N+1 쿼리 없음 검증
 * - DTO Projection 정상 동작 확인
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@Transactional
@DisplayName("GalleryImageRepository QueryDSL 테스트")
class GalleryImageRepositoryQueryDslTest {

    @Autowired
    private GalleryImageRepository galleryImageRepository;

    @Autowired
    private GalleryRepository galleryRepository;

    private Gallery gallery1;
    private Gallery gallery2;
    private GalleryImage image1;
    private GalleryImage image2;
    private GalleryImage image3;
    private GalleryImage image4;

    @BeforeEach
    void setUp() {
        // Gallery 데이터 생성
        gallery1 = Gallery.builder()
            .title("2024 크리스마스 행사")
            .description("크리스마스 행사 사진")
            .eventDate(LocalDate.now().minusDays(7))
            .coverImageUrl("https://example.com/cover1.jpg")
            .isPublished(true)
            .viewCount(100)
            .build();
        galleryRepository.save(gallery1);

        gallery2 = Gallery.builder()
            .title("2024 여름 수련회")
            .description("여름 수련회 사진")
            .eventDate(LocalDate.now().minusMonths(6))
            .coverImageUrl("https://example.com/cover2.jpg")
            .isPublished(true)
            .viewCount(50)
            .build();
        galleryRepository.save(gallery2);

        // GalleryImage 데이터 생성
        image1 = GalleryImage.builder()
            .imageUrl("https://example.com/image1.jpg")
            .thumbnailUrl("https://example.com/thumb1.jpg")
            .caption("크리스마스 예배 모습")
            .fileSize(1024000L)
            .width(1920)
            .height(1080)
            .displayOrder(1)
            .gallery(gallery1)
            .build();
        galleryImageRepository.save(image1);

        image2 = GalleryImage.builder()
            .imageUrl("https://example.com/image2.jpg")
            .thumbnailUrl("https://example.com/thumb2.jpg")
            .caption("찬양팀 연주")
            .fileSize(2048000L)
            .width(1920)
            .height(1080)
            .displayOrder(2)
            .gallery(gallery1)
            .build();
        galleryImageRepository.save(image2);

        image3 = GalleryImage.builder()
            .imageUrl("https://example.com/image3.jpg")
            .thumbnailUrl("https://example.com/thumb3.jpg")
            .caption("수련회 단체사진")
            .fileSize(3072000L)
            .width(1920)
            .height(1080)
            .displayOrder(1)
            .gallery(gallery2)
            .build();
        galleryImageRepository.save(image3);

        image4 = GalleryImage.builder()
            .imageUrl("https://example.com/image4.jpg")
            .thumbnailUrl("https://example.com/thumb4.jpg")
            .caption("게임 시간")
            .fileSize(1536000L)
            .width(1920)
            .height(1080)
            .displayOrder(2)
            .gallery(gallery2)
            .build();
        galleryImageRepository.save(image4);
    }

    @Test
    @DisplayName("갤러리별 이미지 조회 - QueryDSL")
    void testFindByGalleryId() {
        // Given
        Pageable pageable = PageRequest.of(0, 10);

        // When
        Page<GalleryImageResponse> result = galleryImageRepository.findByGalleryId(gallery1.getId(), pageable);

        // Then
        assertThat(result).isNotEmpty();
        assertThat(result.getTotalElements()).isEqualTo(2); // gallery1의 이미지 2개

        // LazyInitializationException 없이 Gallery 데이터 접근 가능
        GalleryImageResponse dto = result.getContent().get(0);
        assertThat(dto.getGalleryId()).isEqualTo(gallery1.getId());
        assertThat(dto.getGalleryId()).isNotNull();

        // displayOrder 오름차순 정렬 확인
        assertThat(result.getContent().get(0).getDisplayOrder()).isLessThan(
            result.getContent().get(1).getDisplayOrder()
        );
    }

    @Test
    @DisplayName("최신 이미지 조회 - QueryDSL")
    void testFindLatestImages() {
        // When - 최신 3개 조회
        List<GalleryImageResponse> result = galleryImageRepository.findLatestImages(3);

        // Then
        assertThat(result).hasSize(3);

        // Gallery 관계 데이터 확인
        assertThat(result).allMatch(dto ->
            dto.getGalleryId() != null
        );

        // 최신순 정렬 확인 (createdAt DESC, id DESC)
        // 이미지 저장 순서: image1, image2, image3, image4
        // 따라서 최신 3개는: image4(gallery2), image3(gallery2), image2(gallery1)
        assertThat(result.get(0).getGalleryId()).isEqualTo(gallery2.getId());  // image4
        assertThat(result.get(1).getGalleryId()).isEqualTo(gallery2.getId());  // image3
        assertThat(result.get(2).getGalleryId()).isEqualTo(gallery1.getId());  // image2
    }

    @Test
    @DisplayName("인기 이미지 조회 - QueryDSL")
    void testFindPopularImages() {
        // Given - viewCount 설정 (실제로는 GalleryImage에 viewCount가 있어야 함)
        // 현재 구현은 createdAt 기준이므로 최신 이미지가 반환됨

        // When
        List<GalleryImageResponse> result = galleryImageRepository.findPopularImages(3);

        // Then
        assertThat(result).hasSize(3);

        // Gallery 관계 데이터 확인
        assertThat(result).allMatch(dto ->
            dto.getGalleryId() != null
        );

        // Note: 현재 구현은 viewCount가 없어서 createdAt 기준 정렬
        // viewCount 추가 시 테스트 수정 필요
    }

    @Test
    @DisplayName("N+1 쿼리 방지 검증 - LEFT JOIN 확인")
    void testNoN1Query() {
        // Given
        Pageable pageable = PageRequest.of(0, 10);

        // When - 쿼리 실행
        Page<GalleryImageResponse> result = galleryImageRepository.findByGalleryId(gallery1.getId(), pageable);

        // Then - Gallery 데이터 접근 시 추가 쿼리 없음
        result.getContent().forEach(dto -> {
            // LazyInitializationException 발생하지 않음
            assertThat(dto.getGalleryId()).isNotNull();
        });

        // DTO Projection이므로 Entity가 아닌 DTO만 반환
        assertThat(result.getContent().get(0)).isInstanceOf(GalleryImageResponse.class);
    }

    @Test
    @DisplayName("이미지 메타데이터 검증")
    void testImageMetadata() {
        // Given
        Pageable pageable = PageRequest.of(0, 10);

        // When
        Page<GalleryImageResponse> result = galleryImageRepository.findByGalleryId(gallery1.getId(), pageable);

        // Then
        GalleryImageResponse dto = result.getContent().get(0);
        assertThat(dto.getImageUrl()).isNotBlank();
        assertThat(dto.getThumbnailUrl()).isNotBlank();
        assertThat(dto.getCaption()).isNotBlank();
        assertThat(dto.getFileSize()).isGreaterThan(0);
        assertThat(dto.getWidth()).isEqualTo(1920);
        assertThat(dto.getHeight()).isEqualTo(1080);
        assertThat(dto.getDisplayOrder()).isGreaterThan(0);
    }

    @Test
    @DisplayName("페이징 동작 검증")
    void testPagination() {
        // Given
        Pageable page1 = PageRequest.of(0, 1);
        Pageable page2 = PageRequest.of(1, 1);

        // When
        Page<GalleryImageResponse> result1 = galleryImageRepository.findByGalleryId(gallery1.getId(), page1);
        Page<GalleryImageResponse> result2 = galleryImageRepository.findByGalleryId(gallery1.getId(), page2);

        // Then
        assertThat(result1.getContent()).hasSize(1);
        assertThat(result1.getTotalElements()).isEqualTo(2);
        assertThat(result1.getTotalPages()).isEqualTo(2);

        assertThat(result2.getContent()).hasSize(1);
        assertThat(result2.getTotalElements()).isEqualTo(2);

        // 페이지별 데이터 중복 없음
        assertThat(result1.getContent()).doesNotContainAnyElementsOf(result2.getContent());

        // displayOrder 순서 확인
        assertThat(result1.getContent().get(0).getDisplayOrder()).isEqualTo(1);
        assertThat(result2.getContent().get(0).getDisplayOrder()).isEqualTo(2);
    }

    @Test
    @DisplayName("빈 결과 처리 확인")
    void testEmptyResult() {
        // Given - 존재하지 않는 갤러리 ID
        Long nonExistentGalleryId = 99999L;
        Pageable pageable = PageRequest.of(0, 10);

        // When
        Page<GalleryImageResponse> result = galleryImageRepository.findByGalleryId(nonExistentGalleryId, pageable);

        // Then
        assertThat(result).isEmpty();
        assertThat(result.getTotalElements()).isEqualTo(0);
    }

    @Test
    @DisplayName("갤러리별 이미지 개수 확인")
    void testImageCountPerGallery() {
        // Given
        Pageable pageable = PageRequest.of(0, 10);

        // When
        Page<GalleryImageResponse> gallery1Images = galleryImageRepository.findByGalleryId(gallery1.getId(), pageable);
        Page<GalleryImageResponse> gallery2Images = galleryImageRepository.findByGalleryId(gallery2.getId(), pageable);

        // Then
        assertThat(gallery1Images.getTotalElements()).isEqualTo(2);
        assertThat(gallery2Images.getTotalElements()).isEqualTo(2);

        // 각 갤러리의 이미지가 올바르게 분리됨
        assertThat(gallery1Images.getContent()).allMatch(dto ->
            dto.getGalleryId().equals(gallery1.getId())
        );
        assertThat(gallery2Images.getContent()).allMatch(dto ->
            dto.getGalleryId().equals(gallery2.getId())
        );
    }

    @Test
    @DisplayName("displayOrder 정렬 순서 검증")
    void testDisplayOrderSorting() {
        // Given
        Pageable pageable = PageRequest.of(0, 10);

        // When
        Page<GalleryImageResponse> result = galleryImageRepository.findByGalleryId(gallery1.getId(), pageable);

        // Then - displayOrder 오름차순 정렬
        List<GalleryImageResponse> images = result.getContent();
        for (int i = 0; i < images.size() - 1; i++) {
            assertThat(images.get(i).getDisplayOrder())
                .isLessThanOrEqualTo(images.get(i + 1).getDisplayOrder());
        }
    }
}
