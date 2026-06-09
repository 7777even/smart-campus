package com.smart.campus.web.mappers;

import com.campus.mappers.BaseMapper;
import com.campus.entity.AiEarlyWarning;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 学业预警 Mapper（学生端）
 */
public interface WebAiEarlyWarningMapper extends BaseMapper<AiEarlyWarning> {

    List<AiEarlyWarning> selectByStudentId(@Param("studentId") Long studentId);
}
