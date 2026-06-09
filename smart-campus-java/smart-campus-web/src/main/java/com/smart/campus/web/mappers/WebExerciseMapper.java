package com.smart.campus.web.mappers;

import com.campus.mappers.BaseMapper;
import com.campus.entity.Exercise;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 习题 Mapper（学生端）
 */
public interface WebExerciseMapper extends BaseMapper<Exercise> {

    List<Exercise> selectByCourseId(@Param("courseId") String courseId);

    Exercise selectById(@Param("id") Long id);
}
