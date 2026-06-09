package com.campus.mappers;

import com.campus.mappers.BaseMapper;
import com.campus.entity.StudentCourse;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 选课 Mapper
 */
public interface StudentCourseMapper extends BaseMapper<StudentCourse> {

    StudentCourse selectByStudentIdAndCourseId(@Param("studentId") Long studentId,
                                               @Param("courseId") Long courseId);

    List<StudentCourse> selectByStudentId(@Param("studentId") Long studentId);

    long countByStudentId(@Param("studentId") Long studentId);

    long countByCourseId(@Param("courseId") Long courseId);

    int deleteByStudentIdAndCourseId(@Param("studentId") Long studentId,
                                     @Param("courseId") Long courseId);

    /**
     * 获取某学生已选课程ID列表
     */
    List<Long> selectCourseIdsByStudentId(@Param("studentId") Long studentId);
}
