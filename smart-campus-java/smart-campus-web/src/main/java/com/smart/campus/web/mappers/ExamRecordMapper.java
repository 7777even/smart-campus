package com.smart.campus.web.mappers;

import com.campus.mappers.BaseMapper;
import com.smart.campus.web.entity.ExamRecord;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 考试记录 Mapper（学生端）
 */
public interface ExamRecordMapper extends BaseMapper<ExamRecord> {

    ExamRecord selectByExamAndStudent(@Param("examId") Long examId,
                                      @Param("studentId") Long studentId);

    List<ExamRecord> selectByStudentId(@Param("studentId") Long studentId);

    int update(ExamRecord record);

    int insert(ExamRecord record);
}
