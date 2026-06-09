package com.smart.campus.web.mappers;

import com.campus.mappers.BaseMapper;
import com.smart.campus.web.entity.LessonResource;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 课时资源 Mapper（学生端）
 */
public interface LessonResourceMapper extends BaseMapper<LessonResource> {

    List<LessonResource> selectByLessonId(@Param("lessonId") Long lessonId);
}
