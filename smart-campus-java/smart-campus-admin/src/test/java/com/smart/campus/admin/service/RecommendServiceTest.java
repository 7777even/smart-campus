package com.smart.campus.admin.service;

import com.smart.campus.admin.mappers.CourseMapper;
import com.smart.campus.admin.mappers.StudentCourseMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * 推荐引擎服务单元测试
 */
@ExtendWith(MockitoExtension.class)
class RecommendServiceTest {

    @Mock
    private CourseMapper courseMapper;

    @Mock
    private StudentCourseMapper studentCourseMapper;

    /**
     * 辅助方法：创建一个按 SQL 内容返回结果的 JdbcTemplate mock
     */
    private JdbcTemplate createMock(List<Map<String, Object>> formatResults) {
        JdbcTemplate mock = mock(JdbcTemplate.class, invocation -> {
            String method = invocation.getMethod().getName();
            if (!"queryForList".equals(method)) return null;
            String sql = invocation.getArgument(0);
            // hotCourses / hot course ids
            if (sql.contains("LEFT JOIN") || sql.contains("LIMIT")) {
                return List.of(1L, 2L);
            }
            // formatCourseResults: 返回单独课程结果（Map 必须可变！formatCourseResults 会 put enrollCount）
            if (sql.contains("SELECT") && sql.contains("FROM biz_course")) {
                return formatResults;
            }
            // recommendResources - GROUP BY category
            if (sql.contains("GROUP BY category")) {
                return List.of("视频", "文档");
            }
            // recommendResources - category preference
            if (sql.contains("ORDER BY") && !sql.contains("downloads DESC")) {
                return List.of(
                        mutableMap("id", 1L, "name", "资源1", "type", "video", "category", "视频"),
                        mutableMap("id", 2L, "name", "资源2", "type", "doc", "category", "文档")
                );
            }
            // recommendResources - hot fallback
            if (sql.contains("ORDER BY downloads DESC")) {
                return List.of(mutableMap("id", 1L, "name", "热门资源", "type", "video", "downloads", 200));
            }
            return List.of();
        });
        return mock;
    }

    /** 创建可变 Map */
    @SuppressWarnings("unchecked")
    private Map<String, Object> mutableMap(Object... kv) {
        Map<String, Object> m = new HashMap<>();
        for (int i = 0; i < kv.length; i += 2) {
            m.put((String) kv[i], kv[i + 1]);
        }
        return m;
    }

    /** 创建可变课程 Map（formatCourseResults 需要 put enrollCount） */
    private Map<String, Object> courseMap(Long id, String name) {
        Map<String, Object> m = new HashMap<>();
        m.put("id", id);
        m.put("name", name);
        return m;
    }

    @Test
    @DisplayName("hotCourses — 返回热门课程列表")
    void hotCourses() {
        JdbcTemplate mock = createMock(List.of(courseMap(1L, "课程A")));
        when(studentCourseMapper.countByCourseId(anyLong())).thenReturn(5L);
        RecommendService service = new RecommendService(mock, courseMapper, studentCourseMapper);

        List<Map<String, Object>> results = service.hotCourses(10);
        assertFalse(results.isEmpty());
    }

    @Test
    @DisplayName("hotCourses — 无热门课程时返回空列表")
    void hotCoursesEmpty() {
        JdbcTemplate mock = mock(JdbcTemplate.class, inv -> {
            if ("queryForList".equals(inv.getMethod().getName())) return List.of();
            return null;
        });
        RecommendService service = new RecommendService(mock, courseMapper, studentCourseMapper);
        assertTrue(service.hotCourses(10).isEmpty());
    }

    @Test
    @DisplayName("recommendCoursesForGuest — 冷启动时返回热门课程")
    void recommendCoursesForGuest() {
        JdbcTemplate mock = createMock(List.of(courseMap(1L, "课程A")));
        when(studentCourseMapper.countByCourseId(1L)).thenReturn(3L);
        RecommendService service = new RecommendService(mock, courseMapper, studentCourseMapper);
        List<Map<String, Object>> results = service.recommendCoursesForGuest(5);
        assertFalse(results.isEmpty());
    }

    @Test
    @DisplayName("recommendCourses — 学生不存在时回退热门推荐")
    void recommendCoursesStudentNotFound() {
        JdbcTemplate mock = mock(JdbcTemplate.class, inv -> {
            String m = inv.getMethod().getName();
            if (!"queryForList".equals(m)) return null;
            String s = inv.getArgument(0);
            if (s.contains("sys_student")) return List.of();
            if (s.contains("LEFT JOIN") || s.contains("LIMIT")) return List.of();
            return List.of();
        });
        RecommendService service = new RecommendService(mock, courseMapper, studentCourseMapper);
        List<Map<String, Object>> results = service.recommendCourses(99L, 10);
        assertNotNull(results);
    }

    @Test
    @DisplayName("peersAlsoEnrolled — 查询同学也在学")
    void peersAlsoEnrolled() {
        JdbcTemplate mock = mock(JdbcTemplate.class, inv -> {
            String m = inv.getMethod().getName();
            if (!"queryForList".equals(m)) return null;
            String s = inv.getArgument(0);
            if (s.contains("JOIN biz_student_course") && s.contains("sc2")) {
                return List.of(2L, 3L);
            }
            if (s.contains("SELECT") && s.contains("FROM biz_course")) {
                long courseId = inv.getArguments().length > 1 ? ((Number) inv.getArguments()[1]).longValue() : 0;
                return List.of(courseMap(courseId, "课程" + courseId));
            }
            return List.of();
        });
        when(studentCourseMapper.countByCourseId(anyLong())).thenReturn(2L);
        RecommendService service = new RecommendService(mock, courseMapper, studentCourseMapper);

        List<Map<String, Object>> results = service.peersAlsoEnrolled(1L, 5);
        assertFalse(results.isEmpty());
        assertEquals(2, results.size());
    }

    @Test
    @DisplayName("recommendResources — 偏好资源推荐")
    void recommendResources() {
        JdbcTemplate mock = createMock(List.of());
        RecommendService service = new RecommendService(mock, courseMapper, studentCourseMapper);
        List<Map<String, Object>> results = service.recommendResources(1L, 5);
        assertFalse(results.isEmpty());
    }

    @Test
    @DisplayName("recommendResources — 无偏好时按热门推荐")
    void recommendResourcesNoPreference() {
        JdbcTemplate mock = mock(JdbcTemplate.class, inv -> {
            String m = inv.getMethod().getName();
            if (!"queryForList".equals(m)) return null;
            String s = inv.getArgument(0);
            if (s.contains("GROUP BY category")) return List.of();
            if (s.contains("ORDER BY downloads DESC")) {
                return List.of(Map.of("id", 1L, "name", "热门资源", "type", "video", "downloads", 200));
            }
            return List.of();
        });
        RecommendService service = new RecommendService(mock, courseMapper, studentCourseMapper);
        List<Map<String, Object>> results = service.recommendResources(1L, 5);
        assertFalse(results.isEmpty());
    }
}
