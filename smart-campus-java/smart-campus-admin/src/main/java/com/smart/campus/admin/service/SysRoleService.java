package com.smart.campus.admin.service;

import com.campus.mappers.BaseMapper;
import com.campus.service.BaseService;
import com.smart.campus.admin.entity.SysRole;
import com.smart.campus.admin.mappers.SysRoleMapper;
import org.springframework.stereotype.Service;

@Service
public class SysRoleService extends BaseService<SysRole> {

    private final SysRoleMapper sysRoleMapper;

    public SysRoleService(SysRoleMapper sysRoleMapper) {
        this.sysRoleMapper = sysRoleMapper;
    }

    @Override
    protected BaseMapper<SysRole> getMapper() {
        return sysRoleMapper;
    }
}
