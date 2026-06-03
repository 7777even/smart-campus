package com.smart.campus.admin.mappers;

import com.campus.mappers.BaseMapper;
import com.smart.campus.admin.entity.SysUser;
import org.apache.ibatis.annotations.Param;

public interface SysUserMapper extends BaseMapper<SysUser> {

    /**
     * 按用户名查询用户
     */
    SysUser selectByUsername(@Param("username") String username);

    /**
     * 切换用户启用/禁用状态
     */
    int toggleStatus(@Param("id") Long id);
}
