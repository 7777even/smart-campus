package com.smart.campus.admin.entity;

import com.campus.entity.BaseEntity;

/**
 * 系统用户实体
 */
public class SysUser extends BaseEntity {

    private String username;
    private String password;
    private String realName;
    private String role;
    private String email;
    private String avatar;
    private String phone;
    private Integer status = 1;    // 0禁用 1启用
    private Long studentId;        // 关联学生ID（仅学生角色）

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public String getRealName() { return realName; }
    public void setRealName(String realName) { this.realName = realName; }
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getAvatar() { return avatar; }
    public void setAvatar(String avatar) { this.avatar = avatar; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public Integer getStatus() { return status; }
    public void setStatus(Integer status) { this.status = status; }
    public Long getStudentId() { return studentId; }
    public void setStudentId(Long studentId) { this.studentId = studentId; }
}
