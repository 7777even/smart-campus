package com.smart.campus.admin.service;

import com.campus.mappers.CourseMapper;
import com.campus.mappers.StudentCourseMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 推荐引擎 — 基于规则的课程/资源个性化推荐
 */
@Service
public class RecommendService {

    private final JdbcTemplate jdbcTemplate;
    private final CourseMapper courseMapper;
    private final StudentCourseMapper studentCourseMapper;

    public RecommendService(JdbcTemplate jdbcTemplate,
                            CourseMapper courseMapper,
                            StudentCourseMapper studentCourseMapper) {
        this.jdbcTemplate = jdbcTemplate;
        this.courseMapper = courseMapper;
        this.studentCourseMapper = studentCourseMapper;
    }

    /**
     * 个性化课程推荐（核心算法）
     */
    public List<Map<String, Object>> recommendCourses(Long studentId, int limit) {
        // 1. 获取学生信息
        Map<String, Object> student = getStudentInfo(studentId);
        if (student == null) {
            return hotCourses(limit);
        }
        Long departmentId = (Long) student.get("department_id");
        Long majorId = (Long) student.get("major_id");

        // 2. 获取已选课程ID
        List<Long> enrolledIds = studentCourseMapper.selectCourseIdsByStudentId(studentId);
        Set<Long> enrolledSet = new HashSet<>(enrolledIds);

        // 3. 获取学生偏好课程类型
        String preferredType = getPreferredCourseType(studentId);

        // 4. 获取所有可选课程（未选）
        String sql = """
            SELECT c.id, c.name, c.code, c.type, c.credit, c.hours, c.teacher_name,
                   c.department_id, c.description, c.cover,
                   c.department_id = ? AS dept_match,
                   (SELECT COUNT(*) FROM biz_student_course WHERE course_id = c.id) AS enroll_count
            FROM biz_course c
            WHERE c.status = 1
            ORDER BY c.create_time DESC
            """;

        List<Map<String, Object>> allCourses = jdbcTemplate.queryForList(sql, departmentId);
        if (allCourses.isEmpty()) {
            return List.of();
        }

        // 5. 获取同专业高GPA学生的选课热度
        Map<Long, Double> peerScoreMap = getPeerEnrollmentScores(majorId, studentId);

        // 6. 获取最大选课数以归一化
        long maxEnroll = getMaxEnrollmentCount();

        // 7. 评分每门课程
        List<ScoredCourse> scored = new ArrayList<>();
        for (Map<String, Object> course : allCourses) {
            Long courseId = ((Number) course.get("id")).longValue();

            // 跳过已选课程
            if (enrolledSet.contains(courseId)) continue;

            double score = 0.0;

            // 同院系匹配 +3.0
            boolean deptMatch = ((Number) course.get("dept_match")).intValue() == 1;
            if (deptMatch) score += 3.0;

            // 同专业大类(同院系专业) +1.5
            if (isRelatedDepartment((Long) course.get("department_id"), departmentId, majorId)) {
                score += 1.5;
            }

            // 同专业高GPA同学选课 +2.0
            Double peerScore = peerScoreMap.get(courseId);
            if (peerScore != null) {
                score += peerScore * 2.0;
            }

            // 热门度 +1.0 (归一化)
            long enrollCount = ((Number) course.get("enroll_count")).longValue();
            if (maxEnroll > 0) {
                score += (double) enrollCount / maxEnroll * 1.0;
            }

            // 新颖性(未选) +1.0 (已经跳过了已选课程)
            score += 1.0;

            // 类型偏好 +0.5
            String type = (String) course.get("type");
            if (preferredType != null && preferredType.equals(type)) {
                score += 0.5;
            }

            ScoredCourse sc = new ScoredCourse();
            sc.courseId = courseId;
            sc.score = score;
            scored.add(sc);
        }

        // 8. 按评分降序排序，取 Top-N
        scored.sort((a, b) -> Double.compare(b.score, a.score));
        List<Long> topIds = scored.stream()
                .filter(s -> s.score > 3.0) // 阈值 > 3.0
                .limit(limit)
                .map(s -> s.courseId)
                .collect(Collectors.toList());

        // 如果推荐数量不够，用热门课程补齐
        if (topIds.size() < limit) {
            List<Long> hotIds = getHotCourseIds(limit * 2);
            for (Long hotId : hotIds) {
                if (!topIds.contains(hotId) && !enrolledSet.contains(hotId)) {
                    topIds.add(hotId);
                    if (topIds.size() >= limit) break;
                }
            }
        }

        return formatCourseResults(topIds);
    }

    /**
     * 冷启动推荐（无学生信息时使用）
     */
    public List<Map<String, Object>> recommendCoursesForGuest(int limit) {
        return hotCourses(limit);
    }

    /**
     * 热门课程推荐
     */
    public List<Map<String, Object>> hotCourses(int limit) {
        List<Long> hotIds = getHotCourseIds(limit);
        return formatCourseResults(hotIds);
    }

    /**
     * 个性化资源推荐
     */
    public List<Map<String, Object>> recommendResources(Long studentId, int limit) {
        int categoryLimit = Math.min(Math.max(limit, 1), 5);
        List<String> categories = getPreferredResourceCategories(studentId, categoryLimit);

        if (categories.isEmpty()) {
            return queryHotResources(limit);
        }
        return queryResourcesByCategories(categories, limit);
    }

    /** 按下载量取热门资源（冷启动） */
    private List<Map<String, Object>> queryHotResources(int limit) {
        return jdbcTemplate.queryForList(
            """
            SELECT id, name, type, category, file_size, downloads, create_time
            FROM biz_resource WHERE status = 1
            ORDER BY downloads DESC, create_time DESC LIMIT ?
            """, limit);
    }

    /**
     * 偏好资源分类：按分类汇总下载量排序。
     * 使用 GROUP BY + SUM(downloads)，避免 DISTINCT 与 ORDER BY 列不一致导致 MySQL 3065 错误。
     */
    private List<String> getPreferredResourceCategories(Long studentId, int limit) {
        // biz_resource 与课程无直接关联，按全站分类热度取 Top-N；studentId 保留供后续扩展
        return jdbcTemplate.queryForList(
            """
            SELECT category
            FROM biz_resource
            WHERE status = 1 AND category IS NOT NULL AND category != ''
            GROUP BY category
            ORDER BY SUM(downloads) DESC
            LIMIT ?
            """, String.class, limit);
    }

    /** 优先匹配偏好分类，再按下载量排序 */
    private List<Map<String, Object>> queryResourcesByCategories(List<String> categories, int limit) {
        String placeholders = String.join(",", Collections.nCopies(categories.size(), "?"));
        List<Object> params = new ArrayList<>(categories);
        params.add(limit);
        return jdbcTemplate.queryForList(
            """
            SELECT id, name, type, category, file_size, downloads, create_time
            FROM biz_resource
            WHERE status = 1
            ORDER BY
                CASE WHEN category IN (%s) THEN 0 ELSE 1 END,
                downloads DESC,
                create_time DESC
            LIMIT ?
            """.formatted(placeholders),
            params.toArray());
    }

    /**
     * 同学也在学 — 选同一门课的学生还选了哪些课
     */
    public List<Map<String, Object>> peersAlsoEnrolled(Long courseId, int limit) {
        List<Long> peerCourseIds = jdbcTemplate.queryForList(
            """
            SELECT sc2.course_id, COUNT(*) AS cnt
            FROM biz_student_course sc1
            JOIN biz_student_course sc2 ON sc1.student_id = sc2.student_id
            WHERE sc1.course_id = ? AND sc2.course_id != ?
            GROUP BY sc2.course_id
            ORDER BY cnt DESC
            LIMIT ?
            """, Long.class, courseId, courseId, limit);

        return formatCourseResults(peerCourseIds);
    }

    // ====================== 私有方法 ======================

    private Map<String, Object> getStudentInfo(Long studentId) {
        try {
            List<Map<String, Object>> rows = jdbcTemplate.queryForList(
                "SELECT id, department_id, major_id FROM sys_student WHERE id = ?", studentId);
            return rows.isEmpty() ? null : rows.get(0);
        } catch (Exception e) {
            return null;
        }
    }

    private String getPreferredCourseType(Long studentId) {
        try {
            List<String> types = jdbcTemplate.queryForList(
                """
                SELECT c.type FROM biz_course c
                JOIN biz_student_course sc ON c.id = sc.course_id
                WHERE sc.student_id = ?
                GROUP BY c.type
                ORDER BY COUNT(*) DESC
                LIMIT 1
                """, String.class, studentId);
            return types.isEmpty() ? null : types.get(0);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 同专业高GPA学生选课热度
     */
    private Map<Long, Double> getPeerEnrollmentScores(Long majorId, Long excludeStudentId) {
        Map<Long, Double> result = new HashMap<>();
        if (majorId == null) return result;

        try {
            // 查询同专业综合评分 >= 75 的学生的选课分布
            List<Map<String, Object>> rows = jdbcTemplate.queryForList(
                """
                SELECT sc.course_id,
                       COUNT(DISTINCT sc.student_id) AS peer_count,
                       (SELECT COUNT(*) FROM sys_student WHERE major_id = ? AND status = '在读') AS total_peers
                FROM biz_student_course sc
                JOIN ai_student_profile asp ON asp.student_id = sc.student_id
                WHERE asp.major_id = ? AND asp.comprehensive_score >= 75
                  AND sc.student_id != ?
                GROUP BY sc.course_id
                """, majorId, majorId, excludeStudentId);

            for (Map<String, Object> row : rows) {
                Long courseId = ((Number) row.get("course_id")).longValue();
                long peerCount = ((Number) row.get("peer_count")).longValue();
                long totalPeers = ((Number) row.get("total_peers")).longValue();
                double rate = totalPeers > 0 ? (double) peerCount / totalPeers : 0;
                result.put(courseId, rate);
            }
        } catch (Exception ignored) {}

        return result;
    }

    private long getMaxEnrollmentCount() {
        try {
            Long max = jdbcTemplate.queryForObject(
                "SELECT MAX(cnt) FROM (SELECT COUNT(*) AS cnt FROM biz_student_course GROUP BY course_id) t",
                Long.class);
            return max != null ? max : 1;
        } catch (Exception e) {
            return 1;
        }
    }

    private List<Long> getHotCourseIds(int limit) {
        try {
            return jdbcTemplate.queryForList(
                """
                SELECT c.id FROM biz_course c
                LEFT JOIN (SELECT course_id, COUNT(*) AS cnt FROM biz_student_course GROUP BY course_id) sc
                    ON c.id = sc.course_id
                WHERE c.status = 1
                ORDER BY COALESCE(sc.cnt, 0) DESC, c.create_time DESC
                LIMIT ?
                """, Long.class, limit);
        } catch (Exception e) {
            return List.of();
        }
    }

    private boolean isRelatedDepartment(Long courseDeptId, Long studentDeptId, Long studentMajorId) {
        if (courseDeptId == null || studentDeptId == null) return false;
        if (courseDeptId.equals(studentDeptId)) return true;
        // 检查课程是否属于学生专业所在的院系
        if (studentMajorId != null) {
            try {
                List<Long> deptIds = jdbcTemplate.queryForList(
                    "SELECT department_id FROM sys_major WHERE id = ?", Long.class, studentMajorId);
                return deptIds.contains(courseDeptId);
            } catch (Exception ignored) {}
        }
        return false;
    }

    private List<Map<String, Object>> formatCourseResults(List<Long> courseIds) {
        if (courseIds.isEmpty()) return List.of();
        List<Map<String, Object>> result = new ArrayList<>();
        for (Long id : courseIds) {
            try {
                List<Map<String, Object>> rows = jdbcTemplate.queryForList(
                    "SELECT id, name, code, type, credit, hours, teacher_name, description, cover FROM biz_course WHERE id = ? AND status = 1",
                    id);
                if (!rows.isEmpty()) {
                    Map<String, Object> course = rows.get(0);
                    long enrollCount = studentCourseMapper.countByCourseId(id);
                    course.put("enrollCount", enrollCount);
                    result.add(course);
                }
            } catch (Exception ignored) {}
        }
        return result;
    }

    /**
     * 带评分的课程结果（内部类）
     */
    private static class ScoredCourse {
        Long courseId;
        double score;
    }
}
