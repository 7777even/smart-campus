package com.smart.campus.web.biz;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.campus.entity.AiConversation;
import com.campus.entity.AiMessage;
import com.smart.campus.web.mappers.WebAiConversationMapper;
import com.smart.campus.web.mappers.WebAiMessageMapper;
import okhttp3.*;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * AI 对话业务（学生端）
 */
@Component
public class AiChatWebBiz {

    private static final String LLM_API_URL = "http://localhost:11434/api/chat";
    private static final String LLM_MODEL = "qwen2.5:7b";

    private final WebAiConversationMapper conversationMapper;
    private final WebAiMessageMapper messageMapper;
    private final OkHttpClient httpClient;

    public AiChatWebBiz(WebAiConversationMapper conversationMapper,
                        WebAiMessageMapper messageMapper) {
        this.conversationMapper = conversationMapper;
        this.messageMapper = messageMapper;
        this.httpClient = new OkHttpClient.Builder()
                .connectTimeout(60, TimeUnit.SECONDS)
                .readTimeout(120, TimeUnit.SECONDS)
                .build();
    }

    /**
     * 创建对话
     */
    public Map<String, Object> createConversation(Long userId, String title) {
        if (title == null || title.isBlank()) {
            title = "新对话";
        }

        AiConversation conv = new AiConversation();
        conv.setUserId(userId);
        conv.setUserRole("student");
        conv.setTitle(title);
        conversationMapper.insert(conv);

        Map<String, Object> m = new HashMap<>();
        m.put("id", String.valueOf(conv.getId()));
        m.put("userId", conv.getUserId());
        m.put("userRole", conv.getUserRole());
        m.put("title", conv.getTitle());
        m.put("createTime", conv.getCreateTime());
        return m;
    }

    /**
     * 发送消息并获取 AI 回复
     */
    public Map<String, Object> sendMessage(String conversationId, String message) {
        if (message == null || message.isBlank()) {
            throw new RuntimeException("消息内容不能为空");
        }

        long convIdLong = Long.valueOf(conversationId);

        // 保存用户消息
        AiMessage userMsg = new AiMessage();
        userMsg.setConversationId(convIdLong);
        userMsg.setRole("user");
        userMsg.setContent(message);
        userMsg.setTokens(message.length());
        messageMapper.insert(userMsg);

        // 获取历史消息
        List<AiMessage> history = messageMapper.selectByConversationId(convIdLong);
        List<String> messagesForLlm = new ArrayList<>();
        for (AiMessage msg : history) {
            messagesForLlm.add(msg.getRole() + ": " + msg.getContent());
        }

        // 调用 LLM API
        String reply;
        try {
            String systemPrompt = "你是 Smart Campus 智能学习助手，帮助学生解答学习相关问题。回答要简洁、准确、有帮助。";
            String requestBody = buildLlmRequestBody(systemPrompt + "\n" + String.join("\n", messagesForLlm));
            String response = callLlmApi(requestBody);

            JSONObject json = JSON.parseObject(response);
            reply = json.getString("message");
            if (reply == null) reply = json.getString("response");
            if (reply == null) reply = "抱歉，暂时无法回答，请稍后再试。";
        } catch (IOException e) {
            reply = "抱歉，AI 服务暂时不可用，请稍后再试。";
        }

        // 保存 AI 回复
        AiMessage assistantMsg = new AiMessage();
        assistantMsg.setConversationId(convIdLong);
        assistantMsg.setRole("assistant");
        assistantMsg.setContent(reply);
        assistantMsg.setTokens(reply.length());
        messageMapper.insert(assistantMsg);

        Map<String, Object> result = new HashMap<>();
        result.put("reply", reply);
        result.put("tokens", reply.length());
        return result;
    }

    /**
     * 获取对话消息列表
     */
    public List<Map<String, Object>> getMessages(String conversationId) {
        List<AiMessage> history = messageMapper.selectByConversationId(Long.valueOf(conversationId));
        List<Map<String, Object>> result = new ArrayList<>();
        for (AiMessage msg : history) {
            Map<String, Object> m = new HashMap<>();
            m.put("id", String.valueOf(msg.getId()));
            m.put("conversationId", msg.getConversationId());
            m.put("role", msg.getRole());
            m.put("content", msg.getContent());
            m.put("tokens", msg.getTokens());
            m.put("createTime", msg.getCreateTime());
            result.add(m);
        }
        return result;
    }

    /**
     * 获取用户的对话列表
     */
    public List<Map<String, Object>> list(Long userId) {
        List<AiConversation> list = conversationMapper.selectByUserId(userId);
        List<Map<String, Object>> result = new ArrayList<>();
        for (AiConversation conv : list) {
            Map<String, Object> m = new HashMap<>();
            m.put("id", String.valueOf(conv.getId()));
            m.put("userId", conv.getUserId());
            m.put("userRole", conv.getUserRole());
            m.put("title", conv.getTitle());
            m.put("createTime", conv.getCreateTime());
            result.add(m);
        }
        return result;
    }

    /**
     * 删除对话
     */
    public void deleteConversation(String id) {
        conversationMapper.deleteById(Long.valueOf(id));
    }

    private String buildLlmRequestBody(String messages) {
        JSONObject body = new JSONObject();
        body.put("model", LLM_MODEL);
        body.put("stream", false);
        body.put("messages", JSONArray.parseArray(
                "[{\"role\":\"system\",\"content\":\"你是 Smart Campus 智能学习助手。\"}," +
                "{\"role\":\"user\",\"content\":\"" + messages + "\"}" +
                "]"));
        return body.toJSONString();
    }

    private String callLlmApi(String requestBody) throws IOException {
        RequestBody body = RequestBody.create(
                requestBody,
                MediaType.parse("application/json"));
        Request request = new Request.Builder()
                .url(LLM_API_URL)
                .post(body)
                .build();
        try (Response response = httpClient.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("LLM API 调用失败: " + response.code());
            }
            ResponseBody rb = response.body();
            return rb != null ? rb.string() : "";
        }
    }
}
