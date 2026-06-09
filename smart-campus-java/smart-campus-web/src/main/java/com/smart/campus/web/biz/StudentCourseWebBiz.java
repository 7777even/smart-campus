package com.smart.campus.web.biz;

import com.campus.entity.Course;
import com.campus.entity.StudentCourse;
import com.campus.mappers.CourseMapper;
import com.campus.mappers.StudentCourseMapper;
import org.springframework.stereotype.Component;

/**
 * 学生选课/退课业务
 */
@Component
public class StudentCourseWebBiz {

    private final CourseMapper courseMapper;
    private final StudentCourseMapper studentCourseMapper;

    public StudentCourseWebBiz(CourseMapper courseMapper,
                               StudentCourseMapper studentCourseMapper) {
        this.courseMapper = courseMapper;
        this.studentCourseMapper = studentCourseMapper;
    }

    /**
     * 选课
     */
    public void enroll(Long studentId, String courseId) {
        Long cid = Long.valueOf(courseId);
        Course course = courseMapper.selectById(cid);
        if (course == null) {
            throw new RuntimeException("课程不存在");
        }
        if (course.getStatus() != 1) {
            throw new RuntimeException("课程已停用");
        }

        StudentCourse existing = studentCourseMapper.selectByStudentIdAndCourseId(studentId, cid);
        if (existing != null) {
            throw new RuntimeException("已选修该课程");
        }

        StudentCourse sc = new StudentCourse();
        sc.setStudentId(studentId);
        sc.setCourseId(cid);
        sc.setScore(null);
        studentCourseMapper.insert(sc);
    }

    /**
     * 退课
     */
    public void drop(Long studentId, String courseId) {
        int deleted = studentCourseMapper.deleteByStudentIdAndCourseId(studentId, Long.valueOf(courseId));
        if (deleted == 0) {
            throw new RuntimeException("未找到选课记录");
        }
    }

    /**
     * 检查是否已选课
     */
    public boolean checkEnrolled(Long studentId, String courseId) {
        StudentCourse sc = studentCourseMapper.selectByStudentIdAndCourseId(studentId, Long.valueOf(courseId));
        return sc != null;
    }
}
