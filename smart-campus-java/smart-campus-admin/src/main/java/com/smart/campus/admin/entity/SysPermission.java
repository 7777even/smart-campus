package com.smart.campus.admin.entity;

import java.util.List;

/**
 * 系统权限/菜单实体
 */
public class SysPermission {

    private Long id;
    private String name;
    private String perms;
    private String path;
    private String icon;
    private Integer sort = 0;
    private Long parentId;
    private Integer type = 1;          // 1菜单 2按钮
    private Integer status = 1;        // 0禁用 1启用
    private List<SysPermission> children;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getPerms() { return perms; }
    public void setPerms(String perms) { this.perms = perms; }
    public String getPath() { return path; }
    public void setPath(String path) { this.path = path; }
    public String getIcon() { return icon; }
    public void setIcon(String icon) { this.icon = icon; }
    public Integer getSort() { return sort; }
    public void setSort(Integer sort) { this.sort = sort; }
    public Long getParentId() { return parentId; }
    public void setParentId(Long parentId) { this.parentId = parentId; }
    public Integer getType() { return type; }
    public void setType(Integer type) { this.type = type; }
    public Integer getStatus() { return status; }
    public void setStatus(Integer status) { this.status = status; }
    public List<SysPermission> getChildren() { return children; }
    public void setChildren(List<SysPermission> children) { this.children = children; }
}
