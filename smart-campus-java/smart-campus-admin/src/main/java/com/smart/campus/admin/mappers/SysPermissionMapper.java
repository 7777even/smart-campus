package com.smart.campus.admin.mappers;

import com.campus.mappers.BaseMapper;
import com.smart.campus.admin.entity.SysPermission;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface SysPermissionMapper extends BaseMapper<SysPermission> {

    /**
     * 按父 ID 查询子权限
     */
    List<SysPermission> selectByParentId(@Param("parentId") Long parentId);

    /**
     * 查询角色已分配的权限 ID 列表
     */
    List<Long> selectPermissionIdsByRoleId(@Param("roleId") Long roleId);

    /**
     * 删除角色的所有权限关联
     */
    int deleteRolePermissions(@Param("roleId") Long roleId);

    /**
     * 为角色分配权限
     */
    int insertRolePermission(@Param("roleId") Long roleId, @Param("permissionId") Long permissionId);
}
