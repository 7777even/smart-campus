package com.campus.mappers;

import com.campus.mappers.BaseMapper;
import com.campus.entity.Department;

import java.util.List;
import java.util.Map;

public interface DepartmentMapper extends BaseMapper<Department> {

    @Override
    List<Department> selectList(Map<String, Object> params);

    @Override
    long selectCount(Map<String, Object> params);
}
