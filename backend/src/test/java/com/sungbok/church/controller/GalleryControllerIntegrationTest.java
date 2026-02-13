package com.sungbok.church.controller;

import com.sungbok.church.domain.entity.Gallery;
import com.sungbok.church.domain.entity.GalleryImage;
import com.sungbok.church.domain.repository.GalleryImageRepository;
import com.sungbok.church.domain.repository.GalleryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import java.time.LocalDate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * GalleryController Integration Tests
 *
 * Tests entity-based endpoints and verifies:
 * - No LazyInitializationException (CRITICAL: gallery association must be eagerly loaded)
 * - Date range filtering
 * - Search functionality
 * - Pagination
 */
@SpringBootTest
@Transactional
@ActiveProfiles("test")
@DisplayName("GalleryController Integration Tests")
class GalleryControllerIntegrationTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    private MockMvc mockMvc;

    @Autowired
    private GalleryRepository galleryRepository;

    @Autowired
    private GalleryImageRepository galleryImageRepository;

    private Gallery gallery1;
    private Gallery gallery2;
    private Gallery gallery3;
    private GalleryImage image1;
    private GalleryImage image2;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        // Create galleries
        gallery1 = Gallery.builder()
                .title("2024 성탄절 행사")
                .description("성탄절 행사 사진")
                .eventDate(LocalDate.now().minusDays(30))
                .coverImageUrl("https://example.com/christmas.jpg")
                .viewCount(150)
                .isPublished(true)
                .build();
        galleryRepository.save(gallery1);

        gallery2 = Gallery.builder()
                .title("2024 부활절 행사")
                .description("부활절 행사 사진")
                .eventDate(LocalDate.now().minusDays(60))
                .coverImageUrl("https://example.com/easter.jpg")
                .viewCount(200)
                .isPublished(true)
                .build();
        galleryRepository.save(gallery2);

        gallery3 = Gallery.builder()
                .title("2024 여름 수련회")
                .description("여름 수련회 사진")
                .eventDate(LocalDate.now().minusDays(90))
                .coverImageUrl("https://example.com/summer.jpg")
                .viewCount(100)
                .isPublished(false)  // Unpublished
                .build();
        galleryRepository.save(gallery3);

        // Create gallery images for gallery1
        image1 = GalleryImage.builder()
                .gallery(gallery1)
                .imageUrl("https://example.com/christmas1.jpg")
                .thumbnailUrl("https://example.com/christmas1_thumb.jpg")
                .caption("성탄절 사진 1")
                .fileSize(1024L)
                .width(1920)
                .height(1080)
                .displayOrder(1)
                .build();
        galleryImageRepository.save(image1);

        image2 = GalleryImage.builder()
                .gallery(gallery1)
                .imageUrl("https://example.com/christmas2.jpg")
                .thumbnailUrl("https://example.com/christmas2_thumb.jpg")
                .caption("성탄절 사진 2")
                .fileSize(2048L)
                .width(1920)
                .height(1080)
                .displayOrder(2)
                .build();
        galleryImageRepository.save(image2);
    }

    @Test
    @DisplayName("GET /api/galleries - Published Galleries List (LazyInitializationException Risk)")
    void getPublishedGalleries_EntityBased_NoLazyLoading() throws Exception {
        mockMvc.perform(get("/api/galleries")
                        .param("page", "0")
                        .param("size", "20"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content[0].id").exists())
                .andExpect(jsonPath("$.content[0].title").exists());
    }

    @Test
    @DisplayName("GET /api/galleries/{id} - Single Gallery (EntityGraph for images)")
    void getGalleryById_WithImages_VerifyEagerLoading() throws Exception {
        mockMvc.perform(get("/api/galleries/" + gallery1.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(gallery1.getId()))
                .andExpect(jsonPath("$.title").value("2024 성탄절 행사"))
                .andExpect(jsonPath("$.description").exists());
    }

    @Test
    @DisplayName("GET /api/galleries/{galleryId}/images - Gallery Images (Critical Test)")
    void getGalleryImages_EntityBased_VerifyGalleryAccess() throws Exception {
        mockMvc.perform(get("/api/galleries/" + gallery1.getId() + "/images"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].galleryId").value(gallery1.getId())) // May trigger lazy loading!
                .andExpect(jsonPath("$[0].imageUrl").exists());
    }

    @Test
    @DisplayName("GET /api/galleries/date-range - Date Range Filter")
    void getGalleriesByDateRange() throws Exception {
        LocalDate start = LocalDate.now().minusDays(100);
        LocalDate end = LocalDate.now();

        mockMvc.perform(get("/api/galleries/date-range")
                        .param("startDate", start.toString())
                        .param("endDate", end.toString())
                        .param("size", "20"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray());
    }

    @Test
    @DisplayName("GET /api/galleries/latest - Latest Galleries")
    void getLatestGalleries() throws Exception {
        mockMvc.perform(get("/api/galleries/latest"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].title").exists());
    }

    @Test
    @DisplayName("GET /api/galleries/search/title - Title Search")
    void searchGalleriesByTitle() throws Exception {
        mockMvc.perform(get("/api/galleries/search/title")
                        .param("keyword", "성탄절")
                        .param("size", "20"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray());
    }

    @Test
    @DisplayName("GET /api/galleries/search - Keyword Search")
    void searchGalleriesByKeyword() throws Exception {
        mockMvc.perform(get("/api/galleries/search")
                        .param("keyword", "행사")
                        .param("size", "20"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray());
    }

    @Test
    @DisplayName("GET /api/galleries/stats/total - Total Count")
    void getTotalGalleryCount() throws Exception {
        mockMvc.perform(get("/api/galleries/stats/total"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isNumber());
    }

    @Test
    @DisplayName("GET /api/galleries/{galleryId}/stats/images - Image Count")
    void getGalleryImageCount() throws Exception {
        mockMvc.perform(get("/api/galleries/" + gallery1.getId() + "/stats/images"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isNumber());
    }

    @Test
    @DisplayName("Pagination Verification")
    void verifyPaginationWorks() throws Exception {
        mockMvc.perform(get("/api/galleries")
                        .param("page", "0")
                        .param("size", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content.length()").value(1))
                .andExpect(jsonPath("$.totalElements").exists())
                .andExpect(jsonPath("$.totalPages").exists());
    }
}
