package com.smart.campus.web.biz;

import com.campus.entity.PageResult;
import com.campus.result.R;
import com.campus.utils.JwtUtil;
import com.campus.utils.PasswordUtil;
import com.campus.entity.SysUser;
import com.campus.mappers.SysUserMapper;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * 用户端认证业务
 */
@Component
public class AuthWebBiz {

    private final SysUserMapper sysUserMapper;

    public AuthWebBiz(SysUserMapper sysUserMapper) {
        this.sysUserMapper = sysUserMapper;
    }

    /**
     * 学生登录
     */
    public Map<String, Object> login(String username, String password) {
        SysUser user = sysUserMapper.selectByUsername(username);
        if (user == null) {
            throw new RuntimeException("用户名或密码错误");
        }
        if (user.getStatus() == 0) {
            throw new RuntimeException("账号已禁用");
        }
        if (!PasswordUtil.matches(password, user.getPassword())) {
            throw new RuntimeException("用户名或密码错误");
        }

        String token = JwtUtil.generate(user.getId(), user.getUsername(), user.getRole(), user.getStudentId());

        Map<String, Object> userInfo = new HashMap<>();
        userInfo.put("id", user.getId());
        userInfo.put("username", user.getUsername());
        userInfo.put("realName", user.getRealName());
        userInfo.put("role", user.getRole());
        userInfo.put("studentId", user.getStudentId());

        Map<String, Object> result = new HashMap<>();
        result.put("token", token);
        result.put("user", userInfo);
        return result;
    }

    /**
     * 获取当前用户详情
     */
    public Map<String, Object> getUserInfo(Long userId) {
        SysUser user = sysUserMapper.selectById(userId);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }

        Map<String, Object> result = new HashMap<>();
        result.put("id", user.getId());
        result.put("username", user.getUsername());
        result.put("realName", user.getRealName());
        result.put("role", user.getRole());
        result.put("email", user.getEmail());
        result.put("avatar", user.getAvatar());
        result.put("phone", user.getPhone());
        result.put("studentId", user.getStudentId());
        return result;
    }
}
