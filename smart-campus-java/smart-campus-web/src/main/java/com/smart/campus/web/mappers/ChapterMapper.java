package com.smart.campus.web.mappers;

import com.campus.mappers.BaseMapper;
import com.smart.campus.web.entity.Chapter;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 章节 Mapper（学生端）
 */
public interface ChapterMapper extends BaseMapper<Chapter> {

    List<Chapter> selectByCourseId(@Param("courseId") String courseId);
}
