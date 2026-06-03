package com.campus.components;

import com.campus.exception.UnauthorizedException;
import com.campus.utils.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class AuthInterceptor implements HandlerInterceptor {

    private static final Logger log = LoggerFactory.getLogger(AuthInterceptor.class);

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        // OPTIONS 预检请求直接放行
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }

        // 登录接口无需认证
        String path = request.getRequestURI();
        if (path.contains("/auth/login") || path.contains("/auth/captcha")) {
            return true;
        }

        // 从 Header 获取 Token
        String token = request.getHeader("Authorization");
        if (token == null || !token.startsWith("Bearer ")) {
            throw new UnauthorizedException("未登录或 Token 已过期");
        }

        token = token.substring(7);
        if (!JwtUtil.validate(token)) {
            throw new UnauthorizedException("Token 无效或已过期");
        }

        // 将用户信息存入请求属性
        request.setAttribute("userId", JwtUtil.getUserId(token));
        request.setAttribute("userRole", JwtUtil.getRole(token));
        return true;
    }
}
