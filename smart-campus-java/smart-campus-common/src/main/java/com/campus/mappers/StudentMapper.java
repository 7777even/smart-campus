package com.campus.mappers;

import com.campus.mappers.BaseMapper;
import com.campus.entity.Student;

import java.util.List;
import java.util.Map;

public interface StudentMapper extends BaseMapper<Student> {

    @Override
    List<Student> selectList(Map<String, Object> params);

    @Override
    long selectCount(Map<String, Object> params);
}
