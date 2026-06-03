package com.smart.campus.admin.controller;

import com.campus.exception.UnauthorizedException;
import com.campus.result.R;
import com.campus.utils.JwtUtil;
import com.campus.utils.PasswordUtil;
import com.smart.campus.admin.entity.SysUser;
import com.smart.campus.admin.mappers.SysUserMapper;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final SysUserMapper sysUserMapper;

    public AuthController(SysUserMapper sysUserMapper) {
        this.sysUserMapper = sysUserMapper;
    }

    /**
     * 登录
     */
    @PostMapping("/login")
    public R<Map<String, Object>> login(@RequestBody Map<String, String> loginForm) {
        String username = loginForm.get("username");
        String password = loginForm.get("password");

        if (username == null || password == null) {
            throw new UnauthorizedException("用户名或密码不能为空");
        }

        SysUser user = sysUserMapper.selectByUsername(username);
        if (user == null) {
            throw new UnauthorizedException("用户名或密码错误");
        }

        if (!PasswordUtil.matches(password, user.getPassword())) {
            throw new UnauthorizedException("用户名或密码错误");
        }

        if (user.getStatus() != null && user.getStatus() == 0) {
            throw new UnauthorizedException("账号已被禁用");
        }

        String token = JwtUtil.generate(user.getId(), user.getUsername(), user.getRole());

        Map<String, Object> userInfo = new LinkedHashMap<>();
        userInfo.put("id", user.getId());
        userInfo.put("username", user.getUsername());
        userInfo.put("realName", user.getRealName());
        userInfo.put("role", user.getRole());

        Map<String, Object> data = new LinkedHashMap<>();
        data.put("token", token);
        data.put("user", userInfo);

        return R.ok(data);
    }

    /**
     * 获取当前用户信息
     */
    @GetMapping("/info")
    public R<Map<String, Object>> info(@RequestAttribute Long userId) {
        SysUser user = sysUserMapper.selectById(userId);
        if (user == null) {
            throw new UnauthorizedException("用户不存在");
        }

        Map<String, Object> userInfo = new LinkedHashMap<>();
        userInfo.put("id", user.getId());
        userInfo.put("username", user.getUsername());
        userInfo.put("realName", user.getRealName());
        userInfo.put("role", user.getRole());
        userInfo.put("email", user.getEmail());
        userInfo.put("avatar", user.getAvatar());
        userInfo.put("phone", user.getPhone());
        userInfo.put("status", user.getStatus());

        return R.ok(userInfo);
    }
}
