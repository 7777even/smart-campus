package com.campus.mappers;

import com.campus.mappers.BaseMapper;
import com.campus.entity.Paper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 试卷 Mapper
 */
public interface PaperMapper extends BaseMapper<Paper> {

    List<Paper> selectList(@Param("keyword") String keyword,
                           @Param("courseId") Long courseId,
                           @Param("status") String status);

    long selectCount(@Param("keyword") String keyword,
                     @Param("courseId") Long courseId,
                     @Param("status") String status);
}
