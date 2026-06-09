package com.smart.campus.web.controller;

import com.campus.result.R;
import com.smart.campus.web.biz.AiChatWebBiz;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * AI 对话控制器（学生端）
 */
@RestController
@RequestMapping("/ai/chat")
@Validated
@Tag(name = "AI对话")
public class AiChatController {

    private final AiChatWebBiz aiChatWebBiz;

    public AiChatController(AiChatWebBiz aiChatWebBiz) {
        this.aiChatWebBiz = aiChatWebBiz;
    }

    @PostMapping("/create")
    @Operation(summary = "创建对话")
    public R<Map<String, Object>> createConversation(
            @RequestAttribute Long userId,
            @RequestBody(required = false) Map<String, String> body) {
        String title = body != null ? body.get("title") : null;
        return R.ok(aiChatWebBiz.createConversation(userId, title));
    }

    @PostMapping("/{id}/message")
    @Operation(summary = "发送消息")
    public R<Map<String, Object>> sendMessage(
            @PathVariable String id,
            @RequestBody Map<String, String> body) {
        String message = body.get("message");
        if (message == null || message.isBlank()) {
            return R.fail("消息内容不能为空");
        }
        return R.ok(aiChatWebBiz.sendMessage(id, message));
    }

    @GetMapping("/{id}/messages")
    @Operation(summary = "获取对话历史")
    public R<List<Map<String, Object>>> getMessages(@PathVariable String id) {
        return R.ok(aiChatWebBiz.getMessages(id));
    }

    @GetMapping("/list")
    @Operation(summary = "获取对话列表")
    public R<List<Map<String, Object>>> list(@RequestAttribute Long userId) {
        return R.ok(aiChatWebBiz.list(userId));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除对话")
    public R<Void> delete(@PathVariable String id) {
        aiChatWebBiz.deleteConversation(id);
        return R.ok();
    }
}
