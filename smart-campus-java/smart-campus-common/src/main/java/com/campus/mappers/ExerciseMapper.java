package com.campus.mappers;

import com.campus.mappers.BaseMapper;
import com.campus.entity.Exercise;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 习题 Mapper
 */
public interface ExerciseMapper extends BaseMapper<Exercise> {

    List<Exercise> selectList(@Param("keyword") String keyword,
                              @Param("type") String type,
                              @Param("difficulty") String difficulty,
                              @Param("courseId") Long courseId);

    long selectCount(@Param("keyword") String keyword,
                     @Param("type") String type,
                     @Param("difficulty") String difficulty,
                     @Param("courseId") Long courseId);
}
