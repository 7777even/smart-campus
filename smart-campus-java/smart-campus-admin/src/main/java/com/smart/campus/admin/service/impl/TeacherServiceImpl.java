package com.smart.campus.admin.service.impl;

import com.campus.mappers.BaseMapper;
import com.campus.service.BaseService;
import com.smart.campus.admin.entity.Teacher;
import com.smart.campus.admin.mappers.TeacherMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 教师服务实现
 */
@Service
public class TeacherServiceImpl extends BaseService<Teacher> {

    private final TeacherMapper teacherMapper;

    @Autowired
    public TeacherServiceImpl(TeacherMapper teacherMapper) {
        this.teacherMapper = teacherMapper;
    }

    @Override
    protected BaseMapper<Teacher> getMapper() {
        return teacherMapper;
    }
}
