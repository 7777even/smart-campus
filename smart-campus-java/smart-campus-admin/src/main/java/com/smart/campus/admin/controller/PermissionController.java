package com.smart.campus.admin.controller;

import com.campus.result.R;
import com.campus.entity.SysPermission;
import com.campus.entity.SysRole;
import com.campus.entity.SysUser;
import com.smart.campus.admin.service.SysPermissionService;
import com.smart.campus.admin.service.SysRoleService;
import com.smart.campus.admin.service.SysUserService;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/permissions")
public class PermissionController {

    private final SysUserService sysUserService;
    private final SysRoleService sysRoleService;
    private final SysPermissionService sysPermissionService;

    public PermissionController(SysUserService sysUserService,
                                SysRoleService sysRoleService,
                                SysPermissionService sysPermissionService) {
        this.sysUserService = sysUserService;
        this.sysRoleService = sysRoleService;
        this.sysPermissionService = sysPermissionService;
    }

    // ==================== 用户管理 ====================

    /**
     * 分页查询用户列表
     */
    @GetMapping("/users")
    public R<?> listUsers(
            @RequestParam(defaultValue = "1") int pageNo,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String role) {
        Map<String, Object> params = new HashMap<>();
        params.put("keyword", keyword);
        params.put("role", role);
        return R.ok(sysUserService.page(pageNo, pageSize, params));
    }

    /**
     * 更新用户
     */
    @PutMapping("/users/{id}")
    public R<Void> updateUser(@PathVariable Long id, @RequestBody SysUser user) {
        user.setId(id);
        sysUserService.update(user);
        return R.ok();
    }

    /**
     * 删除用户
     */
    @DeleteMapping("/users/{id}")
    public R<Void> deleteUser(@PathVariable Long id) {
        sysUserService.delete(id);
        return R.ok();
    }

    /**
     * 切换用户启用/禁用状态
     */
    @PutMapping("/users/{id}/toggle-status")
    public R<Void> toggleUserStatus(@PathVariable Long id) {
        sysUserService.toggleStatus(id);
        return R.ok();
    }

    // ==================== 角色管理 ====================

    /**
     * 查询所有角色
     */
    @GetMapping("/roles")
    public R<?> listRoles() {
        List<SysRole> roles = sysRoleService.list(null);
        return R.ok(roles);
    }

    // ==================== 菜单/权限管理 ====================

    /**
     * 获取权限树（所有权限以树形结构返回）
     */
    @GetMapping("/menus")
    public R<?> listMenus() {
        List<SysPermission> tree = sysPermissionService.getPermissionTree();
        return R.ok(tree);
    }

    /**
     * 为角色分配权限
     */
    @PutMapping("/roles/{roleId}/permissions")
    public R<Void> assignPermissions(@PathVariable Long roleId, @RequestBody List<Long> permissionIds) {
        sysPermissionService.assignPermissions(roleId, permissionIds);
        return R.ok();
    }
}
