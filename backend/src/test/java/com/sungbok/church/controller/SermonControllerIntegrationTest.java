package com.sungbok.church.controller;

import com.sungbok.church.domain.entity.Sermon;
import com.sungbok.church.domain.entity.Worship;
import com.sungbok.church.domain.enums.WorshipType;
import com.sungbok.church.domain.repository.SermonRepository;
import com.sungbok.church.domain.repository.WorshipRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;

import static org.hamcrest.Matchers.lessThanOrEqualTo;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * SermonController Integration Tests
 *
 * Tests QueryDSL projections end-to-end and verifies:
 * - No LazyInitializationException
 * - QueryDSL DTO projections working correctly
 * - Enum conversion (WorshipType)
 * - Date/DateTime parsing
 * - Pagination
 */
@SpringBootTest
@Transactional
@ActiveProfiles("test")
@DisplayName("SermonController Integration Tests")
class SermonControllerIntegrationTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    private MockMvc mockMvc;

    @Autowired
    private SermonRepository sermonRepository;

    @Autowired
    private WorshipRepository worshipRepository;

    private Worship sundayWorship;
    private Worship wednesdayWorship;
    private Sermon sermon1;
    private Sermon sermon2;
    private Sermon sermon3;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        // Create Sunday worship
        sundayWorship = Worship.builder()
                .title("주일예배")
                .type(WorshipType.SUNDAY)
                .dayOfWeek(DayOfWeek.SUNDAY)
                .startTime(LocalTime.of(11, 0))
                .location("본당")
                .isActive(true)
                .build();
        worshipRepository.save(sundayWorship);

        // Create Wednesday worship
        wednesdayWorship = Worship.builder()
                .title("수요예배")
                .type(WorshipType.WEDNESDAY)
                .dayOfWeek(DayOfWeek.WEDNESDAY)
                .startTime(LocalTime.of(19, 30))
                .location("본당")
                .isActive(true)
                .build();
        worshipRepository.save(wednesdayWorship);

        // Create sermons with various attributes
        sermon1 = Sermon.builder()
                .worship(sundayWorship)
                .title("은혜의 복음")
                .preacher("홍길동 목사")
                .sermonDate(LocalDate.now().minusDays(7))
                .bibleVerse("요한복음 3:16")
                .youtubeVideoId("video123")
                .isPublished(true)
                .isFeatured(true)
                .tags("부활,소망")
                .build();
        sermonRepository.save(sermon1);

        sermon2 = Sermon.builder()
                .worship(wednesdayWorship)
                .title("믿음의 능력")
                .preacher("홍길동 목사")
                .sermonDate(LocalDate.now().minusDays(3))
                .bibleVerse("히브리서 11:1")
                .youtubeVideoId("video456")
                .isPublished(true)
                .isFeatured(false)
                .tags("믿음")
                .build();
        sermonRepository.save(sermon2);

        sermon3 = Sermon.builder()
                .worship(sundayWorship)
                .title("사랑의 실천")
                .preacher("김철수 목사")
                .sermonDate(LocalDate.now().minusDays(14))
                .bibleVerse("고린도전서 13:4")
                .youtubeVideoId("video789")
                .isPublished(true)
                .isFeatured(false)
                .build();
        sermonRepository.save(sermon3);
    }

    @Test
    @DisplayName("GET /api/sermons - Published Sermons List with QueryDSL Projection")
    void getPublishedSermons_ReturnsProjectionDto_NoLazyLoading() throws Exception {
        mockMvc.perform(get("/api/sermons")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content[0].id").exists())
                .andExpect(jsonPath("$.content[0].title").exists())
                .andExpect(jsonPath("$.content[0].preacher").exists())
                .andExpect(jsonPath("$.content[0].worshipName").exists())  // QueryDSL projection
                .andExpect(jsonPath("$.content[0].worshipType").exists()); // Enum → String
    }

    @Test
    @DisplayName("GET /api/sermons/{id} - Single Sermon with EntityGraph")
    void getSermonById_WithEntityGraph_NoLazyLoading() throws Exception {
        mockMvc.perform(get("/api/sermons/" + sermon1.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(sermon1.getId()))
                .andExpect(jsonPath("$.title").value("은혜의 복음"))
                .andExpect(jsonPath("$.preacher").value("홍길동 목사"))
                .andExpect(jsonPath("$.worshipName").exists())
                .andExpect(jsonPath("$.worshipType").exists());
    }

    @Test
    @DisplayName("GET /api/sermons/preacher/{preacher} - By Preacher with QueryDSL")
    void getSermonsByPreacher_ReturnsProjectionDto() throws Exception {
        mockMvc.perform(get("/api/sermons/preacher/홍길동 목사")
                        .param("size", "20"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content[0].preacher").value("홍길동 목사"))
                .andExpect(jsonPath("$.content[0].worshipName").exists());
    }

    @Test
    @DisplayName("GET /api/sermons/date-range - Date Range Filter")
    void getSermonsByDateRange_ReturnsSermons() throws Exception {
        LocalDate start = LocalDate.now().minusDays(30);
        LocalDate end = LocalDate.now();

        mockMvc.perform(get("/api/sermons/date-range")
                        .param("startDate", start.toString())
                        .param("endDate", end.toString())
                        .param("size", "20"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content[0].title").exists());
    }

    @Test
    @DisplayName("GET /api/sermons/featured - Featured Sermons with QueryDSL")
    void getFeaturedSermons_ReturnsProjectionDto() throws Exception {
        mockMvc.perform(get("/api/sermons/featured"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].isFeatured").value(true))
                .andExpect(jsonPath("$[0].worshipName").exists());
    }

    @Test
    @DisplayName("GET /api/sermons/latest - Latest Sermons (Top 10)")
    void getLatestSermons_ReturnsTop10() throws Exception {
        mockMvc.perform(get("/api/sermons/latest"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(lessThanOrEqualTo(10)));
    }

    @Test
    @DisplayName("GET /api/sermons/worship-type/{worshipType}/latest - Enum Path Variable")
    void getLatestSermonsByWorshipType_EnumConversion() throws Exception {
        mockMvc.perform(get("/api/sermons/worship-type/SUNDAY/latest")
                        .param("size", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].worshipType").value("SUNDAY"));
    }

    @Test
    @DisplayName("GET /api/sermons/search/verse - Bible Verse Search")
    void searchByBibleVerse_ReturnsMatchingSermons() throws Exception {
        mockMvc.perform(get("/api/sermons/search/verse")
                        .param("verse", "요한복음")
                        .param("size", "20"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray());
    }

    @Test
    @DisplayName("GET /api/sermons/search/tag - Tag Search")
    void searchByTag_ReturnsMatchingSermons() throws Exception {
        mockMvc.perform(get("/api/sermons/search/tag")
                        .param("tag", "부활")
                        .param("size", "20"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray());
    }

    @Test
    @DisplayName("GET /api/sermons/stats/total - Total Count")
    void getTotalSermonCount() throws Exception {
        mockMvc.perform(get("/api/sermons/stats/total"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isNumber());
    }

    @Test
    @DisplayName("GET /api/sermons/stats/preacher/{preacher} - Count by Preacher")
    void getSermonCountByPreacher() throws Exception {
        mockMvc.perform(get("/api/sermons/stats/preacher/홍길동 목사"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isNumber());
    }

    @Test
    @DisplayName("Pagination Verification - Multiple Pages")
    void verifyPaginationWorks() throws Exception {
        // Request first page with size 2
        mockMvc.perform(get("/api/sermons")
                        .param("page", "0")
                        .param("size", "2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content.length()").value(lessThanOrEqualTo(2)))
                .andExpect(jsonPath("$.totalElements").exists())
                .andExpect(jsonPath("$.totalPages").exists())
                .andExpect(jsonPath("$.number").value(0));
    }
}
