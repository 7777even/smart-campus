package com.campus.exception;

import com.campus.result.R;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 全局异常处理器单元测试
 * <p>
 * 验证 GlobalExceptionHandler 对各种异常类型的转换逻辑。
 * 不启动 Spring 容器，直接调用 handler 方法。
 */
@ExtendWith(MockitoExtension.class)
class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    @DisplayName("BusinessException → 返回业务错误码和消息")
    void handleBusinessException() {
        BusinessException e = new BusinessException(1001, "业务校验失败");
        R<Void> r = handler.handleBusinessException(e);

        assertEquals(1001, r.getCode());
        assertEquals("业务校验失败", r.getMsg());
        assertNull(r.getData());
    }

    @Test
    @DisplayName("BusinessException 默认构造器使用 500")
    void businessExceptionDefaultCode() {
        BusinessException e = new BusinessException("默认错误");
        assertEquals(500, e.getCode());
        assertEquals("默认错误", e.getMessage());
    }

    @Test
    @DisplayName("BusinessException setter 工作正常")
    void businessExceptionSetter() {
        BusinessException e = new BusinessException("test");
        e.setCode(999);
        assertEquals(999, e.getCode());
    }

    @Test
    @DisplayName("UnauthorizedException → 返回 401")
    void handleUnauthorizedException() {
        UnauthorizedException e = new UnauthorizedException("未登录或登录已过期");
        R<Void> r = handler.handleUnauthorizedException(e);

        assertEquals(401, r.getCode());
        assertEquals("未登录或登录已过期", r.getMsg());
    }

    @Test
    @DisplayName("通用 Exception → 返回 500 服务器内部错误")
    void handleGenericException() {
        Exception e = new RuntimeException("数据库连接失败");
        R<Void> r = handler.handleException(e);

        assertEquals(500, r.getCode());
        assertTrue(r.getMsg().contains("数据库连接失败"));
    }

    @Test
    @DisplayName("通用 Exception 包含异常消息")
    void handleGenericExceptionContainsMessage() {
        Exception e = new NullPointerException("null reference");
        R<Void> r = handler.handleException(e);
        assertTrue(r.getMsg().contains("null reference"));
    }
}
