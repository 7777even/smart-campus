package com.smart.campus.web.mappers;

import com.campus.mappers.BaseMapper;
import com.campus.entity.Exam;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 考试 Mapper（学生端）
 */
public interface WebExamMapper extends BaseMapper<Exam> {

    Exam selectById(@Param("id") Long id);

    List<Exam> selectByCourseId(@Param("courseId") String courseId);
}
