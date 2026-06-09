package com.smart.campus.web.mappers;

import com.campus.mappers.BaseMapper;
import com.smart.campus.web.entity.LearningProgress;
import org.apache.ibatis.annotations.Param;

/**
 * 学习进度 Mapper（学生端）
 */
public interface LearningProgressMapper extends BaseMapper<LearningProgress> {

    LearningProgress selectByStudentAndCourse(@Param("studentId") Long studentId,
                                              @Param("courseId") Long courseId);

    int update(LearningProgress progress);

    int insert(LearningProgress progress);
}
