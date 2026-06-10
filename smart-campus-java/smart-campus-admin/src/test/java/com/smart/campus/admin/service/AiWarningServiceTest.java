package com.smart.campus.admin.service;

import com.campus.entity.AiEarlyWarning;
import com.campus.entity.AiStudentProfile;
import com.campus.mappers.AiEarlyWarningMapper;
import com.campus.mappers.AiStudentProfileMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * 学业预警引擎服务单元测试
 */
@ExtendWith(MockitoExtension.class)
class AiWarningServiceTest {

    @Mock
    private AiStudentProfileMapper profileMapper;

    @Mock
    private AiEarlyWarningMapper warningMapper;

    @InjectMocks
    private AiWarningService warningService;

    @BeforeEach
    void setUp() {
        // @Value 字段在 Mockito 中不会被注入，需手动设置
        ReflectionTestUtils.setField(warningService, "attendanceThreshold", 0.7);
        ReflectionTestUtils.setField(warningService, "homeworkThreshold", 60.0);
        ReflectionTestUtils.setField(warningService, "examThreshold", 60.0);
        ReflectionTestUtils.setField(warningService, "redAttendanceThreshold", 0.5);
        ReflectionTestUtils.setField(warningService, "redHomeworkThreshold", 40.0);
        ReflectionTestUtils.setField(warningService, "redExamThreshold", 40.0);
    }

    private AiStudentProfile createProfile(Long id, BigDecimal attendanceRate,
                                           BigDecimal homeworkAvg, BigDecimal examAvg,
                                           BigDecimal comprehensiveScore) {
        AiStudentProfile p = new AiStudentProfile();
        p.setId(id);
        p.setStudentId(id);
        p.setStudentName("测试学生" + id);
        p.setAttendanceRate(attendanceRate);
        p.setHomeworkAvg(homeworkAvg);
        p.setExamAvg(examAvg);
        p.setComprehensiveScore(comprehensiveScore);
        return p;
    }

    @Test
    @DisplayName("evaluateProfile — 画像不存在抛异常")
    void evaluateProfileNotFound() {
        when(profileMapper.selectById(99L)).thenReturn(null);
        assertThrows(RuntimeException.class, () -> warningService.evaluateProfile(99L));
    }

    @Test
    @DisplayName("evaluateProfile — 所有维度正常时不生成预警")
    void evaluateProfileNoWarnings() {
        AiStudentProfile profile = createProfile(1L,
                BigDecimal.valueOf(0.95), BigDecimal.valueOf(85), BigDecimal.valueOf(90), BigDecimal.valueOf(88));
        when(profileMapper.selectById(1L)).thenReturn(profile);

        List<AiEarlyWarning> warnings = warningService.evaluateProfile(1L);

        assertTrue(warnings.isEmpty());
        assertEquals("green", profile.getRiskLevel());
        verify(profileMapper).update(profile);
    }

    @Test
    @DisplayName("evaluateProfile — 出勤率偏低生成黄色预警")
    void evaluateProfileAttendanceYellow() {
        AiStudentProfile profile = createProfile(1L,
                BigDecimal.valueOf(0.60), BigDecimal.valueOf(85), BigDecimal.valueOf(90), BigDecimal.valueOf(80));
        when(profileMapper.selectById(1L)).thenReturn(profile);

        List<AiEarlyWarning> warnings = warningService.evaluateProfile(1L);

        assertFalse(warnings.isEmpty());
        boolean hasAttendance = warnings.stream()
                .anyMatch(w -> "attendance".equals(w.getWarningType()) && "yellow".equals(w.getLevel()));
        assertTrue(hasAttendance);
    }

    @Test
    @DisplayName("evaluateProfile — 出勤率极低生成红色预警")
    void evaluateProfileAttendanceRed() {
        AiStudentProfile profile = createProfile(1L,
                BigDecimal.valueOf(0.40), BigDecimal.valueOf(85), BigDecimal.valueOf(90), BigDecimal.valueOf(80));
        when(profileMapper.selectById(1L)).thenReturn(profile);

        List<AiEarlyWarning> warnings = warningService.evaluateProfile(1L);

        boolean hasAttendance = warnings.stream()
                .anyMatch(w -> "attendance".equals(w.getWarningType()) && "red".equals(w.getLevel()));
        assertTrue(hasAttendance);
    }

    @Test
    @DisplayName("evaluateProfile — 考试成绩偏低生成红色预警")
    void evaluateProfileExamRed() {
        AiStudentProfile profile = createProfile(1L,
                BigDecimal.valueOf(0.90), BigDecimal.valueOf(85), BigDecimal.valueOf(35), BigDecimal.valueOf(70));
        when(profileMapper.selectById(1L)).thenReturn(profile);

        List<AiEarlyWarning> warnings = warningService.evaluateProfile(1L);

        boolean hasExam = warnings.stream()
                .anyMatch(w -> "exam".equals(w.getWarningType()) && "red".equals(w.getLevel()));
        assertTrue(hasExam);
    }

    @Test
    @DisplayName("evaluateProfile — 综合评分偏低生成黄色预警")
    void evaluateProfileComprehensiveYellow() {
        AiStudentProfile profile = createProfile(1L,
                BigDecimal.valueOf(0.90), BigDecimal.valueOf(85), BigDecimal.valueOf(90), BigDecimal.valueOf(65));
        when(profileMapper.selectById(1L)).thenReturn(profile);

        List<AiEarlyWarning> warnings = warningService.evaluateProfile(1L);

        boolean hasComprehensive = warnings.stream()
                .anyMatch(w -> "comprehensive".equals(w.getWarningType()) && "yellow".equals(w.getLevel()));
        assertTrue(hasComprehensive);
    }

    @Test
    @DisplayName("evaluateProfile — 综合评分低于60生成红色预警并更新风险等级")
    void evaluateProfileComprehensiveRed() {
        AiStudentProfile profile = createProfile(1L,
                BigDecimal.valueOf(0.90), BigDecimal.valueOf(85), BigDecimal.valueOf(90), BigDecimal.valueOf(55));
        when(profileMapper.selectById(1L)).thenReturn(profile);

        List<AiEarlyWarning> warnings = warningService.evaluateProfile(1L);

        boolean hasComprehensive = warnings.stream()
                .anyMatch(w -> "comprehensive".equals(w.getWarningType()) && "red".equals(w.getLevel()));
        assertTrue(hasComprehensive);
        assertEquals("red", profile.getRiskLevel());
        verify(profileMapper).update(profile);
    }

    @Test
    @DisplayName("evaluateAll — 批量评估所有画像")
    void evaluateAll() {
        AiStudentProfile p1 = createProfile(1L,
                BigDecimal.valueOf(0.40), BigDecimal.valueOf(85), BigDecimal.valueOf(90), BigDecimal.valueOf(80));
        AiStudentProfile p2 = createProfile(2L,
                BigDecimal.valueOf(0.95), BigDecimal.valueOf(85), BigDecimal.valueOf(90), BigDecimal.valueOf(88));

        when(profileMapper.selectList(anyMap())).thenReturn(List.of(p1, p2));
        when(profileMapper.selectById(1L)).thenReturn(p1);
        when(profileMapper.selectById(2L)).thenReturn(p2);

        Map<String, Object> result = warningService.evaluateAll();

        assertEquals(2, result.get("totalProfiles"));
        assertTrue((Integer) result.get("totalWarnings") >= 1);
    }

    @Test
    @DisplayName("resolveWarning — 解决预警")
    void resolveWarning() {
        AiEarlyWarning warning = new AiEarlyWarning();
        warning.setId(1L);
        warning.setStatus("pending");

        when(warningMapper.selectById(1L)).thenReturn(warning);

        AiEarlyWarning resolved = warningService.resolveWarning(1L, "辅导员赵");

        assertEquals("resolved", resolved.getStatus());
        assertEquals("辅导员赵", resolved.getResolver());
        assertNotNull(resolved.getResolveTime());
        verify(warningMapper).update(warning);
    }

    @Test
    @DisplayName("resolveWarning — 预警不存在抛异常")
    void resolveWarningNotFound() {
        when(warningMapper.selectById(99L)).thenReturn(null);
        assertThrows(RuntimeException.class, () -> warningService.resolveWarning(99L, "admin"));
    }

    @Test
    @DisplayName("getStatistics — 返回各状态和等级统计")
    void getStatistics() {
        when(warningMapper.selectCount(anyMap())).thenReturn(5L, 3L, 1L, 4L, 2L);

        Map<String, Object> stats = warningService.getStatistics();

        assertEquals(5L, stats.get("pendingCount"));
        assertEquals(3L, stats.get("resolvedCount"));
        assertEquals(1L, stats.get("ignoredCount"));
        assertEquals(4L, stats.get("redCount"));
        assertEquals(2L, stats.get("yellowCount"));
    }

    @Test
    @DisplayName("page — 分页查询预警")
    void page() {
        when(warningMapper.selectCount(anyMap())).thenReturn(20L);
        when(warningMapper.selectList(anyMap())).thenReturn(List.of(new AiEarlyWarning()));

        var result = warningService.page(1, 10, new HashMap<>(Map.of("level", "red")));

        assertEquals(20, result.getTotalCount());
        assertEquals(1, result.getList().size());
    }
}
