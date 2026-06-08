package com.campus.result;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 统一响应体 R 单元测试
 */
class RTest {

    @Test
    @DisplayName("ok() — 无数据成功响应")
    void okNoData() {
        R<Void> r = R.ok();
        assertEquals(200, r.getCode());
        assertEquals("操作成功", r.getMsg());
        assertNull(r.getData());
    }

    @Test
    @DisplayName("ok(T data) — 带数据成功响应")
    void okWithData() {
        R<String> r = R.ok("hello");
        assertEquals(200, r.getCode());
        assertEquals("操作成功", r.getMsg());
        assertEquals("hello", r.getData());
    }

    @Test
    @DisplayName("ok(msg, data) — 自定义消息的成功响应")
    void okWithMsgAndData() {
        R<List<String>> r = R.ok("查询成功", List.of("a", "b"));
        assertEquals(200, r.getCode());
        assertEquals("查询成功", r.getMsg());
        assertEquals(2, r.getData().size());
    }

    @Test
    @DisplayName("fail(msg) — 默认错误码 500")
    void failWithMsg() {
        R<Void> r = R.fail("系统繁忙");
        assertEquals(500, r.getCode());
        assertEquals("系统繁忙", r.getMsg());
        assertNull(r.getData());
    }

    @Test
    @DisplayName("fail(code, msg) — 自定义错误码")
    void failWithCodeAndMsg() {
        R<Void> r = R.fail(1001, "参数错误");
        assertEquals(1001, r.getCode());
        assertEquals("参数错误", r.getMsg());
    }

    @Test
    @DisplayName("unauthorized — 返回 401")
    void unauthorized() {
        R<Void> r = R.unauthorized("未登录");
        assertEquals(401, r.getCode());
        assertEquals("未登录", r.getMsg());
    }

    @Test
    @DisplayName("forbidden — 返回 403")
    void forbidden() {
        R<Void> r = R.forbidden("无权限");
        assertEquals(403, r.getCode());
        assertEquals("无权限", r.getMsg());
    }

    @Test
    @DisplayName("setter/getter 正确工作")
    void settersAndGetters() {
        R<String> r = new R<>();
        r.setCode(200);
        r.setMsg("test");
        r.setData("value");
        assertEquals(200, r.getCode());
        assertEquals("test", r.getMsg());
        assertEquals("value", r.getData());
    }
}
