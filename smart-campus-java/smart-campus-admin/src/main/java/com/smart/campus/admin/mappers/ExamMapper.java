package com.smart.campus.admin.mappers;

import com.campus.mappers.BaseMapper;
import com.smart.campus.admin.entity.Exam;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 考试 Mapper
 */
public interface ExamMapper extends BaseMapper<Exam> {

    List<Exam> selectList(@Param("keyword") String keyword,
                          @Param("status") String status,
                          @Param("startDate") String startDate,
                          @Param("endDate") String endDate);

    long selectCount(@Param("keyword") String keyword,
                     @Param("status") String status,
                     @Param("startDate") String startDate,
                     @Param("endDate") String endDate);
}
