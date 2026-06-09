package com.campus.mappers;

import com.campus.mappers.BaseMapper;
import com.campus.entity.Course;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 课程 Mapper
 */
public interface CourseMapper extends BaseMapper<Course> {

    List<Course> selectList(@Param("keyword") String keyword,
                            @Param("departmentId") Long departmentId,
                            @Param("type") String type,
                            @Param("credit") Integer credit,
                            @Param("pageNo") Integer pageNo,
                            @Param("pageSize") Integer pageSize);

    long selectCount(@Param("keyword") String keyword,
                     @Param("departmentId") Long departmentId,
                     @Param("type") String type,
                     @Param("credit") Integer credit);

    List<Course> selectBatchByIds(@Param("ids") List<Long> ids);
}
