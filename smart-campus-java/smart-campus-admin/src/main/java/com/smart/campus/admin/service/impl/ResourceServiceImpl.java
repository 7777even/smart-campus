package com.smart.campus.admin.service.impl;

import com.campus.mappers.BaseMapper;
import com.campus.service.BaseService;
import com.smart.campus.admin.entity.Resource;
import com.smart.campus.admin.mappers.ResourceMapper;
import org.springframework.stereotype.Service;

/**
 * 资源 Service 实现
 */
@Service
public class ResourceServiceImpl extends BaseService<Resource> {

    private final ResourceMapper resourceMapper;

    public ResourceServiceImpl(ResourceMapper resourceMapper) {
        this.resourceMapper = resourceMapper;
    }

    @Override
    protected BaseMapper<Resource> getMapper() {
        return resourceMapper;
    }
}
