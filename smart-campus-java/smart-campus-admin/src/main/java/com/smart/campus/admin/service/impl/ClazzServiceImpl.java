package com.smart.campus.admin.service.impl;

import com.campus.mappers.BaseMapper;
import com.campus.service.BaseService;
import com.campus.entity.Clazz;
import com.campus.mappers.ClazzMapper;
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
