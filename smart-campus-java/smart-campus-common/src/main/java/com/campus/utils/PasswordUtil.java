package com.campus.utils;

import org.springframework.util.DigestUtils;

import java.nio.charset.StandardCharsets;

/**
 * 密码工具类（简化版，实际生产建议用 BCrypt）
 */
public class PasswordUtil {

    public static String encode(String rawPassword) {
        return DigestUtils.md5DigestAsHex((rawPassword + "{smart-campus}").getBytes(StandardCharsets.UTF_8));
    }

    public static boolean matches(String rawPassword, String encodedPassword) {
        return encode(rawPassword).equals(encodedPassword);
    }
}
