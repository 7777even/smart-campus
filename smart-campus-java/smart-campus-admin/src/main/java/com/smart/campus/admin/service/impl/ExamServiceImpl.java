package com.smart.campus.admin.service.impl;

import com.campus.mappers.BaseMapper;
import com.campus.service.BaseService;
import com.smart.campus.admin.entity.Exam;
import com.smart.campus.admin.mappers.ExamMapper;
import org.springframework.stereotype.Service;

/**
 * 考试 Service 实现
 */
@Service
public class ExamServiceImpl extends BaseService<Exam> {

    private final ExamMapper examMapper;

    public ExamServiceImpl(ExamMapper examMapper) {
        this.examMapper = examMapper;
    }

    @Override
    protected BaseMapper<Exam> getMapper() {
        return examMapper;
    }
}
