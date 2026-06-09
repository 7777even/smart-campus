package com.smart.campus.web.biz;

import com.campus.entity.Course;
import com.campus.entity.Resource;
import com.campus.entity.StudentCourse;
import com.campus.mappers.CourseMapper;
import com.campus.mappers.StudentCourseMapper;
import com.smart.campus.web.mappers.WebResourceMapper;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * 推荐业务（学生端）
 */
@Component
public class RecommendWebBiz {

    private final CourseMapper courseMapper;
    private final StudentCourseMapper studentCourseMapper;
    private final WebResourceMapper resourceMapper;

    public RecommendWebBiz(CourseMapper courseMapper,
                           StudentCourseMapper studentCourseMapper,
                           WebResourceMapper resourceMapper) {
        this.courseMapper = courseMapper;
        this.studentCourseMapper = studentCourseMapper;
        this.resourceMapper = resourceMapper;
    }

    /**
     * 个性化课程推荐（基于学生已选课程的类型/院系，推荐同类型课程）
     */
    public List<Map<String, Object>> getCourses(Long studentId, int limit) {
        List<Long> enrolledCourseIds = studentCourseMapper.selectCourseIdsByStudentId(studentId);

        if (enrolledCourseIds.isEmpty()) {
            return getHotCourses(limit);
        }

        List<Course> enrolledCourses = courseMapper.selectBatchByIds(enrolledCourseIds);
        Set<String> courseTypes = new HashSet<>();
        Set<Long> enrolledIds = new HashSet<>();
        for (Course c : enrolledCourses) {
            if (c.getType() != null) {
                courseTypes.add(c.getType());
            }
            enrolledIds.add(c.getId());
        }

        List<Map<String, Object>> result = new ArrayList<>();
        for (String type : courseTypes) {
            Map<String, Object> params = new HashMap<>();
            params.put("keyword", null);
            params.put("departmentId", null);
            params.put("type", type);
            params.put("credit", null);
            params.put("pageNo", 0);
            params.put("pageSize", limit * 2);
            List<Course> similarCourses = courseMapper.selectList(params);
            for (Course c : similarCourses) {
                if (!enrolledIds.contains(c.getId())) {
                    Map<String, Object> m = courseToMap(c);
                    result.add(m);
                    if (result.size() >= limit) break;
                }
            }
            if (result.size() >= limit) break;
        }

        if (result.size() < limit) {
            List<Map<String, Object>> hotCourses = getHotCourses(limit - result.size());
            result.addAll(hotCourses);
        }

        return result.subList(0, Math.min(limit, result.size()));
    }

    /**
     * 访客热门课程推荐
     */
    public List<Map<String, Object>> getCoursesForGuest(int limit) {
        return getHotCourses(limit);
    }

    /**
     * 热门课程推荐
     */
    public List<Map<String, Object>> getHotCourses(int limit) {
        Map<String, Object> params = new HashMap<>();
        params.put("keyword", null);
        params.put("departmentId", null);
        params.put("type", null);
        params.put("credit", null);
        params.put("pageNo", 0);
        params.put("pageSize", limit);
        List<Course> courses = courseMapper.selectList(params);

        List<Map<String, Object>> result = new ArrayList<>();
        for (Course c : courses) {
            Map<String, Object> m = courseToMap(c);
            long enrollCount = studentCourseMapper.countByCourseId(c.getId());
            m.put("enrollCount", enrollCount);
            result.add(m);
        }
        return result;
    }

    /**
     * 个性化资源推荐
     */
    public List<Map<String, Object>> getResources(Long studentId, int limit) {
        List<Long> courseIds = studentCourseMapper.selectCourseIdsByStudentId(studentId);
        if (courseIds.isEmpty()) {
            return getHotResources(limit);
        }

        List<Resource> allResources = new ArrayList<>();
        for (Long courseId : courseIds) {
            List<Resource> resources = resourceMapper.selectByCourseId(String.valueOf(courseId));
            allResources.addAll(resources);
        }

        allResources.sort((a, b) -> Integer.compare(b.getDownloads(), a.getDownloads()));
        List<Map<String, Object>> result = new ArrayList<>();
        for (Resource r : allResources.subList(0, Math.min(limit, allResources.size()))) {
            Map<String, Object> m = new HashMap<>();
            m.put("id", String.valueOf(r.getId()));
            m.put("name", r.getName());
            m.put("type", r.getType());
            m.put("category", r.getCategory());
            m.put("filePath", r.getFilePath());
            m.put("downloads", r.getDownloads());
            result.add(m);
        }
        return result;
    }

    /**
     * 热门资源推荐（访客用）
     */
    public List<Map<String, Object>> getHotResources(int limit) {
        Map<String, Object> params = new HashMap<>();
        params.put("keyword", null);
        params.put("type", null);
        params.put("category", null);
        params.put("pageNo", 0);
        params.put("pageSize", limit * 3);
        List<Resource> allResources = resourceMapper.selectList(params);

        allResources.sort((a, b) -> Integer.compare(b.getDownloads(), a.getDownloads()));
        List<Map<String, Object>> result = new ArrayList<>();
        for (Resource r : allResources.subList(0, Math.min(limit, allResources.size()))) {
            Map<String, Object> m = new HashMap<>();
            m.put("id", String.valueOf(r.getId()));
            m.put("name", r.getName());
            m.put("type", r.getType());
            m.put("category", r.getCategory());
            m.put("filePath", r.getFilePath());
            m.put("downloads", r.getDownloads());
            result.add(m);
        }
        return result;
    }

    /**
     * 同学也在学
     */
    public List<Map<String, Object>> peersAlsoEnrolled(String courseId, int limit) {
        Long courseIdLong = Long.valueOf(courseId);
        long enrolledCount = studentCourseMapper.countByCourseId(courseIdLong);
        if (enrolledCount < 2) {
            return Collections.emptyList();
        }

        // 获取所有选修该课程的学生，再推荐他们也选修的其他课程
        List<Long> studentIds = new ArrayList<>();
        // 通过学生课程记录反查 -- 这里简化：直接推荐同类型热门课程
        Map<String, Object> params = new HashMap<>();
        params.put("keyword", null);
        params.put("departmentId", null);
        params.put("type", null);
        params.put("credit", null);
        params.put("pageNo", 0);
        params.put("pageSize", limit);
        List<Course> courses = courseMapper.selectList(params);

        List<Map<String, Object>> result = new ArrayList<>();
        for (Course c : courses) {
            if (!c.getId().equals(courseIdLong)) {
                Map<String, Object> m = courseToMap(c);
                long count = studentCourseMapper.countByCourseId(c.getId());
                if (count > 0) {
                    m.put("enrollCount", count);
                    result.add(m);
                }
            }
            if (result.size() >= limit) break;
        }
        return result;
    }

    private Map<String, Object> courseToMap(Course c) {
        Map<String, Object> m = new HashMap<>();
        m.put("id", String.valueOf(c.getId()));
        m.put("name", c.getName());
        m.put("type", c.getType());
        m.put("credit", c.getCredit());
        m.put("hours", c.getHours());
        m.put("teacherName", c.getTeacherName());
        m.put("description", c.getDescription());
        m.put("cover", c.getCover());
        return m;
    }
}
