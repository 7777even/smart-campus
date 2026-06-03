package com.smart.campus.admin.mappers;

import com.campus.mappers.BaseMapper;
import com.smart.campus.admin.entity.Course;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 课程 Mapper
 */
public interface CourseMapper extends BaseMapper<Course> {

    List<Course> selectList(@Param("keyword") String keyword,
                            @Param("departmentId") Long departmentId,
                            @Param("type") String type,
                            @Param("credit") Integer credit);

    long selectCount(@Param("keyword") String keyword,
                     @Param("departmentId") Long departmentId,
                     @Param("type") String type,
                     @Param("credit") Integer credit);
}
