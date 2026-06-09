package com.smart.campus.web.mappers;

import com.campus.mappers.BaseMapper;
import com.campus.entity.Resource;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 资源 Mapper（学生端）
 */
public interface WebResourceMapper extends BaseMapper<Resource> {

    Resource selectById(@Param("id") Long id);

    List<Resource> selectByCourseId(@Param("courseId") String courseId);
}
