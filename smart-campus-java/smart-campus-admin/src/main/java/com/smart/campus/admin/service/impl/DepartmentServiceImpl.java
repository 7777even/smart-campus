package com.smart.campus.admin.service.impl;

import com.campus.mappers.BaseMapper;
import com.campus.service.BaseService;
import com.smart.campus.admin.entity.Department;
import com.smart.campus.admin.mappers.DepartmentMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 院系服务实现
 */
@Service
public class DepartmentServiceImpl extends BaseService<Department> {

    private final DepartmentMapper departmentMapper;

    @Autowired
    public DepartmentServiceImpl(DepartmentMapper departmentMapper) {
        this.departmentMapper = departmentMapper;
    }

    @Override
    protected BaseMapper<Department> getMapper() {
        return departmentMapper;
    }
}
