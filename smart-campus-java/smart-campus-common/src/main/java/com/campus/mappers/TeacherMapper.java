package com.campus.mappers;

import com.campus.mappers.BaseMapper;
import com.campus.entity.Teacher;

import java.util.List;
import java.util.Map;

public interface TeacherMapper extends BaseMapper<Teacher> {

    @Override
    List<Teacher> selectList(Map<String, Object> params);

    @Override
    long selectCount(Map<String, Object> params);
}
