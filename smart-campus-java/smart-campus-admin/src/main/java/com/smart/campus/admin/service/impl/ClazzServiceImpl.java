package com.smart.campus.admin.service.impl;

import com.campus.mappers.BaseMapper;
import com.campus.service.BaseService;
import com.smart.campus.admin.entity.Clazz;
import com.smart.campus.admin.mappers.ClazzMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 班级服务实现
 */
@Service
public class ClazzServiceImpl extends BaseService<Clazz> {

    private final ClazzMapper clazzMapper;

    @Autowired
    public ClazzServiceImpl(ClazzMapper clazzMapper) {
        this.clazzMapper = clazzMapper;
    }

    @Override
    protected BaseMapper<Clazz> getMapper() {
        return clazzMapper;
    }
}
