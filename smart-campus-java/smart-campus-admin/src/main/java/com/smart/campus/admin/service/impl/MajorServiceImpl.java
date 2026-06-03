package com.smart.campus.admin.service.impl;

import com.campus.mappers.BaseMapper;
import com.campus.service.BaseService;
import com.smart.campus.admin.entity.Major;
import com.smart.campus.admin.mappers.MajorMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 专业服务实现
 */
@Service
public class MajorServiceImpl extends BaseService<Major> {

    private final MajorMapper majorMapper;

    @Autowired
    public MajorServiceImpl(MajorMapper majorMapper) {
        this.majorMapper = majorMapper;
    }

    @Override
    protected BaseMapper<Major> getMapper() {
        return majorMapper;
    }
}
