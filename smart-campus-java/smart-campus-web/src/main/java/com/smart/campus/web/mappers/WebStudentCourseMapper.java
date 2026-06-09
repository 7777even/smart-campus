package com.smart.campus.web.mappers;

import com.campus.mappers.BaseMapper;
import com.campus.entity.StudentCourse;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 选课 Mapper（学生端）
 */
public interface WebStudentCourseMapper extends BaseMapper<StudentCourse> {

    List<Long> selectCourseIdsByStudentId(@Param("studentId") Long studentId);

    StudentCourse selectByStudentIdAndCourseId(@Param("studentId") Long studentId,
                                                @Param("courseId") Long courseId);
}
