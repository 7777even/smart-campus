package com.smart.campus.web.controller;

import com.campus.result.R;
import com.smart.campus.web.biz.AuthWebBiz;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 认证控制器（学生端）
 */
@RestController
@RequestMapping("/auth")
@Validated
@Tag(name = "认证")
public class AuthController {

    private final AuthWebBiz authWebBiz;

    public AuthController(AuthWebBiz authWebBiz) {
        this.authWebBiz = authWebBiz;
    }

    @PostMapping("/login")
    @Operation(summary = "学生登录")
    public R<Map<String, Object>> login(@RequestBody Map<String, String> loginForm) {
        String username = loginForm.get("username");
        String password = loginForm.get("password");
        return R.ok(authWebBiz.login(username, password));
    }

    @GetMapping("/info")
    @Operation(summary = "获取当前学生信息")
    public R<Map<String, Object>> info(@RequestAttribute Long userId) {
        return R.ok(authWebBiz.getUserInfo(userId));
    }
}
