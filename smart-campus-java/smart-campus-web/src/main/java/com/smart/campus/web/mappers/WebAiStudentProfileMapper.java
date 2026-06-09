package com.smart.campus.web.mappers;

import com.campus.mappers.BaseMapper;
import com.campus.entity.AiStudentProfile;
import org.apache.ibatis.annotations.Param;

/**
 * 学生学业画像 Mapper（学生端）
 */
public interface WebAiStudentProfileMapper extends BaseMapper<AiStudentProfile> {

    AiStudentProfile selectByStudentId(@Param("studentId") Long studentId);
}
