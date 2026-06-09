package com.smart.campus.web.biz;

import com.campus.entity.PageResult;
import com.campus.entity.Course;
import com.campus.entity.StudentCourse;
import com.campus.mappers.CourseMapper;
import com.campus.mappers.StudentCourseMapper;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * 课程业务（学生端）
 */
@Component
public class CourseWebBiz {

    private final CourseMapper courseMapper;
    private final StudentCourseMapper studentCourseMapper;

    public CourseWebBiz(CourseMapper courseMapper,
                        StudentCourseMapper studentCourseMapper) {
        this.courseMapper = courseMapper;
        this.studentCourseMapper = studentCourseMapper;
    }

    /**
     * 课程分页列表（过滤管理端字段）
     */
    public PageResult<Map<String, Object>> pageList(int pageNo, int pageSize, Map<String, Object> params) {
        String keyword = (String) params.get("keyword");
        Long departmentId = (Long) params.get("departmentId");
        String type = (String) params.get("type");
        Integer credit = (Integer) params.get("credit");

        long total = courseMapper.selectCount(keyword, departmentId, type, credit);
        int offset = (pageNo - 1) * pageSize;
        List<Course> courses = courseMapper.selectList(keyword, departmentId, type, credit, offset, pageSize);

        List<Map<String, Object>> list = new ArrayList<>();
        for (Course c : courses) {
            long enrollCount = studentCourseMapper.countByCourseId(c.getId());
            Map<String, Object> m = courseToStudentMap(c);
            m.put("enrollCount", enrollCount);
            list.add(m);
        }

        return new PageResult<>(total, pageSize, pageNo, list);
    }

    /**
     * 课程详情（id 为 String）
     */
    public Map<String, Object> getDetail(String id) {
        Long courseId = Long.valueOf(id);
        Course course = courseMapper.selectById(courseId);
        if (course == null) {
            throw new RuntimeException("课程不存在");
        }

        Map<String, Object> map = courseToStudentMap(course);
        long enrollCount = studentCourseMapper.countByCourseId(courseId);
        map.put("enrollCount", enrollCount);
        return map;
    }

    /**
     * 热门课程（按选课人数排序）
     */
    public List<Map<String, Object>> getHotCourses(int limit) {
        List<Course> courses = courseMapper.selectList(null, null, null, null, 0, limit * 3);

        Map<Long, Long> enrollMap = new HashMap<>();
        for (Course c : courses) {
            long count = studentCourseMapper.countByCourseId(c.getId());
            enrollMap.put(c.getId(), count);
        }

        courses.sort((a, b) -> Long.compare(
                enrollMap.getOrDefault(b.getId(), 0L),
                enrollMap.getOrDefault(a.getId(), 0L)));

        List<Map<String, Object>> result = new ArrayList<>();
        for (int i = 0; i < Math.min(limit, courses.size()); i++) {
            Map<String, Object> m = courseToStudentMap(courses.get(i));
            m.put("enrollCount", enrollMap.getOrDefault(courses.get(i).getId(), 0L));
            result.add(m);
        }
        return result;
    }

    /**
     * 我的选课课程列表
     */
    public List<Map<String, Object>> getMyCourses(Long studentId) {
        if (studentId == null) {
            return Collections.emptyList();
        }
        List<Long> courseIds = studentCourseMapper.selectCourseIdsByStudentId(studentId);
        if (courseIds.isEmpty()) {
            return Collections.emptyList();
        }

        List<Course> courses = courseMapper.selectBatchByIds(courseIds);
        List<Map<String, Object>> result = new ArrayList<>();
        for (Course c : courses) {
            Map<String, Object> m = courseToStudentMap(c);
            m.put("enrollCount", studentCourseMapper.countByCourseId(c.getId()));
            StudentCourse sc = studentCourseMapper.selectByStudentIdAndCourseId(studentId, c.getId());
            if (sc != null) {
                m.put("score", sc.getScore());
            }
            result.add(m);
        }
        return result;
    }

    /**
     * 检查是否已选课
     */
    public boolean checkEnrolled(Long studentId, String courseId) {
        StudentCourse sc = studentCourseMapper.selectByStudentIdAndCourseId(studentId, Long.valueOf(courseId));
        return sc != null;
    }

    /**
     * 我的课表
     */
    public List<Map<String, Object>> getSchedule(Long studentId) {
        List<Map<String, Object>> courses = getMyCourses(studentId);
        for (Map<String, Object> c : courses) {
            c.put("scheduleTime", "待安排");
            c.put("location", "待安排");
            c.put("enrolled", true);
        }
        return courses;
    }

    /**
     * 课程实体转学生端 Map（过滤管理端字段）
     */
    private Map<String, Object> courseToStudentMap(Course c) {
        Map<String, Object> m = new HashMap<>();
        m.put("id", String.valueOf(c.getId()));
        m.put("name", c.getName());
        m.put("type", c.getType());
        m.put("credit", c.getCredit());
        m.put("hours", c.getHours());
        m.put("teacherName", c.getTeacherName());
        m.put("description", c.getDescription());
        m.put("cover", c.getCover());
        m.put("status", c.getStatus());
        m.put("createTime", c.getCreateTime());
        m.put("updateTime", c.getUpdateTime());
        return m;
    }
}
