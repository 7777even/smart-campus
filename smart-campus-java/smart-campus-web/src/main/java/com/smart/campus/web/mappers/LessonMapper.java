package com.smart.campus.web.mappers;

import com.campus.mappers.BaseMapper;
import com.smart.campus.web.entity.Lesson;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 课时 Mapper（学生端）
 */
public interface LessonMapper extends BaseMapper<Lesson> {

    List<Lesson> selectByChapterId(@Param("chapterId") Long chapterId);

    List<Lesson> selectByCourseId(@Param("courseId") String courseId);

    Lesson selectById(@Param("id") Long id);
}
