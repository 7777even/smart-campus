package com.campus.mappers;

import com.campus.mappers.BaseMapper;
import com.campus.entity.AiEarlyWarning;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 学业预警记录 Mapper
 */
public interface AiEarlyWarningMapper extends BaseMapper<AiEarlyWarning> {

    @Override
    List<AiEarlyWarning> selectList(Map<String, Object> params);

    @Override
    long selectCount(Map<String, Object> params);

    List<AiEarlyWarning> selectByStudentId(@Param("studentId") Long studentId);
}
