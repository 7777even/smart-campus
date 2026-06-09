package com.smart.campus.admin.service.impl;

import com.campus.mappers.BaseMapper;
import com.campus.service.BaseService;
import com.campus.entity.Resource;
import com.campus.mappers.ResourceMapper;
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
