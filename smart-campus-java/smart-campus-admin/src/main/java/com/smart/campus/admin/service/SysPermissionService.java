package com.smart.campus.admin.service;

import com.campus.mappers.BaseMapper;
import com.campus.service.BaseService;
import com.smart.campus.admin.entity.SysPermission;
import com.smart.campus.admin.mappers.SysPermissionMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class SysPermissionService extends BaseService<SysPermission> {

    private final SysPermissionMapper sysPermissionMapper;

    public SysPermissionService(SysPermissionMapper sysPermissionMapper) {
        this.sysPermissionMapper = sysPermissionMapper;
    }

    @Override
    protected BaseMapper<SysPermission> getMapper() {
        return sysPermissionMapper;
    }

    /**
     * 获取权限树（所有权限按 parentId 组织为树形结构）
     */
    public List<SysPermission> getPermissionTree() {
        List<SysPermission> all = sysPermissionMapper.selectList(null);
        List<SysPermission> tree = new ArrayList<>();
        for (SysPermission perm : all) {
            if (perm.getParentId() == null || perm.getParentId() == 0) {
                tree.add(buildChildren(perm, all));
            }
        }
        return tree;
    }

    private SysPermission buildChildren(SysPermission parent, List<SysPermission> all) {
        List<SysPermission> children = new ArrayList<>();
        for (SysPermission perm : all) {
            if (parent.getId().equals(perm.getParentId())) {
                children.add(buildChildren(perm, all));
            }
        }
        parent.setChildren(children);
        return parent;
    }

    /**
     * 查询角色已分配的权限 ID 列表
     */
    public List<Long> getPermissionIdsByRoleId(Long roleId) {
        return sysPermissionMapper.selectPermissionIdsByRoleId(roleId);
    }

    /**
     * 为角色分配权限
     */
    @Transactional
    public void assignPermissions(Long roleId, List<Long> permissionIds) {
        sysPermissionMapper.deleteRolePermissions(roleId);
        if (permissionIds != null && !permissionIds.isEmpty()) {
            for (Long permissionId : permissionIds) {
                sysPermissionMapper.insertRolePermission(roleId, permissionId);
            }
        }
    }
}
