package com.smart.campus.admin.service;

import com.campus.mappers.BaseMapper;
import com.campus.service.BaseService;
import com.smart.campus.admin.entity.SysUser;
import com.smart.campus.admin.mappers.SysUserMapper;
import org.springframework.stereotype.Service;

@Service
public class SysUserService extends BaseService<SysUser> {

    private final SysUserMapper sysUserMapper;

    public SysUserService(SysUserMapper sysUserMapper) {
        this.sysUserMapper = sysUserMapper;
    }

    @Override
    protected BaseMapper<SysUser> getMapper() {
        return sysUserMapper;
    }

    public SysUser getByUsername(String username) {
        return sysUserMapper.selectByUsername(username);
    }

    public int toggleStatus(Long id) {
        return sysUserMapper.toggleStatus(id);
    }
}
