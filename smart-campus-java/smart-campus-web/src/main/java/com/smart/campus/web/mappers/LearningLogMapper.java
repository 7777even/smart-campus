package com.smart.campus.web.mappers;

import com.campus.mappers.BaseMapper;
import com.smart.campus.web.entity.LearningLog;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 学习日志 Mapper（学生端）
 */
public interface LearningLogMapper extends BaseMapper<LearningLog> {

    int insert(LearningLog log);

    List<LearningLog> selectByStudentId(@Param("studentId") Long studentId,
                                        @Param("pageNo") Integer pageNo,
                                        @Param("pageSize") Integer pageSize);

    long countByStudentId(@Param("studentId") Long studentId);
}
