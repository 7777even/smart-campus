package com.smart.campus.admin.mappers;

import com.campus.mappers.BaseMapper;
import com.smart.campus.admin.entity.Department;

import java.util.List;
import java.util.Map;

public interface DepartmentMapper extends BaseMapper<Department> {

    @Override
    List<Department> selectList(Map<String, Object> params);

    @Override
    long selectCount(Map<String, Object> params);
}
