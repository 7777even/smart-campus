package com.smart.campus.admin.service.impl;

import com.campus.exception.BusinessException;
import com.campus.mappers.BaseMapper;
import com.campus.service.BaseService;
import com.smart.campus.admin.entity.Course;
import com.smart.campus.admin.entity.StudentCourse;
import com.smart.campus.admin.mappers.CourseMapper;
import com.smart.campus.admin.mappers.StudentCourseMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 选课 Service
 */
@Service
public class StudentCourseServiceImpl extends BaseService<StudentCourse> {

    private final StudentCourseMapper studentCourseMapper;
    private final CourseMapper courseMapper;

    public StudentCourseServiceImpl(StudentCourseMapper studentCourseMapper, CourseMapper courseMapper) {
        this.studentCourseMapper = studentCourseMapper;
        this.courseMapper = courseMapper;
    }

    @Override
    protected BaseMapper<StudentCourse> getMapper() {
        return studentCourseMapper;
    }

    /**
     * 学生选课
     */
    @Transactional
    public void enroll(Long studentId, Long courseId) {
        // 检查课程是否存在
        Course course = courseMapper.selectById(courseId);
        if (course == null) {
            throw new BusinessException("课程不存在");
        }
        // 检查是否已选
        StudentCourse existing = studentCourseMapper.selectByStudentIdAndCourseId(studentId, courseId);
        if (existing != null) {
            throw new BusinessException("已选过该课程");
        }
        // 选课
        StudentCourse sc = new StudentCourse();
        sc.setStudentId(studentId);
        sc.setCourseId(courseId);
        studentCourseMapper.insert(sc);
    }

    /**
     * 学生退课
     */
    @Transactional
    public void drop(Long studentId, Long courseId) {
        StudentCourse existing = studentCourseMapper.selectByStudentIdAndCourseId(studentId, courseId);
        if (existing == null) {
            throw new BusinessException("未选该课程");
        }
        studentCourseMapper.deleteById(existing.getId());
    }

    /**
     * 获取学生已选课程列表（含课程详情）
     */
    public List<Course> getMyCourses(Long studentId) {
        List<Long> courseIds = studentCourseMapper.selectCourseIdsByStudentId(studentId);
        if (courseIds.isEmpty()) {
            return List.of();
        }
        return courseMapper.selectBatchByIds(courseIds);
    }

    /**
     * 检查是否已选某课程
     */
    public boolean isEnrolled(Long studentId, Long courseId) {
        return studentCourseMapper.selectByStudentIdAndCourseId(studentId, courseId) != null;
    }

    /**
     * 获取学生已选课程数量
     */
    public long countByStudentId(Long studentId) {
        return studentCourseMapper.countByStudentId(studentId);
    }

    /**
     * 获取课程选课人数
     */
    public long countByCourseId(Long courseId) {
        return studentCourseMapper.countByCourseId(courseId);
    }

    /**
     * 获取学生已选课程ID列表
     */
    public List<Long> getEnrolledCourseIds(Long studentId) {
        return studentCourseMapper.selectCourseIdsByStudentId(studentId);
    }
}
