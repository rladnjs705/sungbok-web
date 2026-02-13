package com.sungbok.church.controller;

import com.sungbok.church.domain.entity.Worship;
import com.sungbok.church.domain.entity.YouTubeLive;
import com.sungbok.church.domain.enums.LiveStatus;
import com.sungbok.church.domain.enums.WorshipType;
import com.sungbok.church.domain.repository.WorshipRepository;
import com.sungbok.church.domain.repository.YouTubeLiveRepository;
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

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * YouTubeLiveController Integration Tests
 *
 * Tests entity-based endpoints and verifies:
 * - No LazyInitializationException (CRITICAL: worship association must be eagerly loaded)
 * - Enum conversion (LiveStatus)
 * - DateTime parameter parsing
 */
@SpringBootTest
@Transactional
@ActiveProfiles("test")
@DisplayName("YouTubeLiveController Integration Tests")
class YouTubeLiveControllerIntegrationTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    private MockMvc mockMvc;

    @Autowired
    private YouTubeLiveRepository youtubeLiveRepository;

    @Autowired
    private WorshipRepository worshipRepository;

    private Worship sundayWorship;
    private YouTubeLive liveLive;
    private YouTubeLive upcomingLive;
    private YouTubeLive completedLive;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        // Create worship
        sundayWorship = Worship.builder()
                .title("주일예배")
                .type(WorshipType.SUNDAY)
                .dayOfWeek(DayOfWeek.SUNDAY)
                .startTime(LocalTime.of(11, 0))
                .location("본당")
                .isActive(true)
                .build();
        worshipRepository.save(sundayWorship);

        // Create live streaming (LIVE status)
        liveLive = YouTubeLive.builder()
                .worship(sundayWorship)
                .youtubeVideoId("live123")
                .title("현재 생방송")
                .description("주일예배 생방송")
                .scheduledStartTime(LocalDateTime.now().minusHours(1))
                .actualStartTime(LocalDateTime.now().minusHours(1))
                .status(LiveStatus.LIVE)
                .viewerCount(150)
                .build();
        youtubeLiveRepository.save(liveLive);

        // Create upcoming live (UPCOMING status)
        upcomingLive = YouTubeLive.builder()
                .worship(sundayWorship)
                .youtubeVideoId("upcoming456")
                .title("예정된 생방송")
                .description("다음 주일예배")
                .scheduledStartTime(LocalDateTime.now().plusDays(7))
                .status(LiveStatus.UPCOMING)
                .build();
        youtubeLiveRepository.save(upcomingLive);

        // Create completed live (COMPLETED status)
        completedLive = YouTubeLive.builder()
                .worship(sundayWorship)
                .youtubeVideoId("completed789")
                .title("지난 생방송")
                .description("저번주 주일예배")
                .scheduledStartTime(LocalDateTime.now().minusDays(7))
                .actualStartTime(LocalDateTime.now().minusDays(7))
                .endTime(LocalDateTime.now().minusDays(7).plusHours(1))
                .status(LiveStatus.COMPLETED)
                .viewerCount(200)
                .build();
        youtubeLiveRepository.save(completedLive);
    }

    @Test
    @DisplayName("GET /api/youtube-lives/status/{status} - By Status (LazyInitializationException Risk)")
    void getLivesByStatus_EntityBased_ShouldNotThrowLazyException() throws Exception {
        mockMvc.perform(get("/api/youtube-lives/status/LIVE"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].status").value("LIVE"))
                .andExpect(jsonPath("$[0].worshipName").exists()); // May trigger lazy loading!
    }

    @Test
    @DisplayName("GET /api/youtube-lives/current - Current Live Streams")
    void getCurrentLiveStreams_EntityBased_VerifyWorshipAccess() throws Exception {
        mockMvc.perform(get("/api/youtube-lives/current"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].worshipName").exists());
    }

    @Test
    @DisplayName("GET /api/youtube-lives/upcoming - Upcoming Streams")
    void getUpcomingLiveStreams() throws Exception {
        mockMvc.perform(get("/api/youtube-lives/upcoming"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].status").value("UPCOMING"))
                .andExpect(jsonPath("$[0].worshipName").exists());
    }

    @Test
    @DisplayName("GET /api/youtube-lives/video/{videoId} - By Video ID")
    void getLiveByVideoId() throws Exception {
        mockMvc.perform(get("/api/youtube-lives/video/live123"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.youtubeVideoId").value("live123"))
                .andExpect(jsonPath("$.title").value("현재 생방송"))
                .andExpect(jsonPath("$.worshipName").exists());
    }

    @Test
    @DisplayName("GET /api/youtube-lives/completed - Completed Since Date")
    void getCompletedLivesSince_DateTimeParam() throws Exception {
        String since = LocalDateTime.now().minusDays(30).toString();

        mockMvc.perform(get("/api/youtube-lives/completed")
                        .param("since", since))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    @DisplayName("GET /api/youtube-lives/latest - Latest Lives")
    void getLatestLives() throws Exception {
        mockMvc.perform(get("/api/youtube-lives/latest"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].worshipName").exists());
    }

    @Test
    @DisplayName("Enum Path Variable Conversion - LiveStatus")
    void verifyEnumPathVariableConversion() throws Exception {
        // Test all enum values
        mockMvc.perform(get("/api/youtube-lives/status/LIVE"))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/youtube-lives/status/UPCOMING"))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/youtube-lives/status/COMPLETED"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("DateTime Parameter Parsing - ISO Format")
    void verifyDateTimeParameterParsing() throws Exception {
        // Test ISO datetime format
        String isoDateTime = LocalDateTime.now().minusDays(1).toString();

        mockMvc.perform(get("/api/youtube-lives/completed")
                        .param("since", isoDateTime))
                .andExpect(status().isOk());
    }
}
