package com.smart.campus.admin.controller;

import com.campus.entity.PageResult;
import com.campus.result.R;
import com.smart.campus.admin.entity.AiConversation;
import com.smart.campus.admin.entity.AiMessage;
import com.smart.campus.admin.service.AiChatService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * AI 对话控制器
 */
@RestController
@RequestMapping("/ai/chat")
@Tag(name = "AI 对话")
public class AiChatController {

    private final AiChatService aiChatService;

    public AiChatController(AiChatService aiChatService) {
        this.aiChatService = aiChatService;
    }

    @PostMapping("/create")
    @Operation(summary = "创建新对话")
    public R<AiConversation> createConversation(@RequestAttribute Long userId,
                                                 @RequestAttribute String userRole,
                                                 @RequestBody(required = false) Map<String, String> body) {
        String title = body != null ? body.get("title") : null;
        AiConversation conversation = aiChatService.createConversation(userId, userRole, title);
        return R.ok(conversation);
    }

    @PostMapping("/{id}/message")
    @Operation(summary = "发送消息")
    public R<Map<String, Object>> sendMessage(@PathVariable Long id,
                                               @RequestBody Map<String, String> body) {
        String content = body.get("content");
        if (content == null || content.trim().isEmpty()) {
            return R.fail("消息内容不能为空");
        }
        Map<String, Object> result = aiChatService.sendMessage(id, content);
        return R.ok(result);
    }

    @GetMapping("/{id}/messages")
    @Operation(summary = "获取对话历史")
    public R<List<AiMessage>> getMessages(@PathVariable Long id) {
        List<AiMessage> messages = aiChatService.getHistory(id);
        return R.ok(messages);
    }

    @GetMapping("/list")
    @Operation(summary = "获取对话列表")
    public R<List<AiConversation>> list(@RequestAttribute Long userId) {
        List<AiConversation> list = aiChatService.getUserConversations(userId);
        return R.ok(list);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除对话")
    public R<Void> delete(@PathVariable Long id) {
        aiChatService.deleteConversation(id);
        return R.ok();
    }
}
