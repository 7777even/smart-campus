package com.campus.mappers;

import com.campus.mappers.BaseMapper;
import com.campus.entity.AiStudentProfile;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 学生学业画像 Mapper
 */
public interface AiStudentProfileMapper extends BaseMapper<AiStudentProfile> {

    @Override
    List<AiStudentProfile> selectList(Map<String, Object> params);

    @Override
    long selectCount(Map<String, Object> params);

    AiStudentProfile selectByStudentId(@Param("studentId") Long studentId);

    int deleteByStudentId(@Param("studentId") Long studentId);
}
