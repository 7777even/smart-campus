package com.smart.campus.admin.service;

import com.campus.entity.AiConversation;
import com.campus.entity.AiKnowledgeDoc;
import com.campus.entity.AiMessage;
import com.campus.mappers.AiConversationMapper;
import com.campus.mappers.AiKnowledgeDocMapper;
import com.campus.mappers.AiMessageMapper;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * AI 对话服务
 * <p>
 * 核心功能：对话管理、LLM API 调用、RAG 知识增强
 * LLM 调用采用 OkHttp 直连 OpenAI 兼容 API（支持 DeepSeek / 通义千问等）
 */
@Service
public class AiChatService {

    private static final String SYSTEM_PROMPT = "你是一个智慧校园 AI 助教，帮助师生解答校园相关问题。" +
            "你可以回答关于课程、考试、通知、校园服务等问题。" +
            "请用中文回答，保持专业友好。" +
            "如果不知道答案，请如实告知，不要编造信息。";

    private final AiConversationMapper conversationMapper;
    private final AiMessageMapper messageMapper;
    private final AiKnowledgeDocMapper knowledgeDocMapper;
    private final OkHttpClient httpClient;

    @Value("${ai.llm.api-key:sk-placeholder}")
    private String apiKey;

    @Value("${ai.llm.base-url:https://api.deepseek.com}")
    private String baseUrl;

    @Value("${ai.llm.model:deepseek-chat}")
    private String model;

    @Value("${ai.chat.max-history:20}")
    private int maxHistory;

    public AiChatService(AiConversationMapper conversationMapper,
                         AiMessageMapper messageMapper,
                         AiKnowledgeDocMapper knowledgeDocMapper) {
        this.conversationMapper = conversationMapper;
        this.messageMapper = messageMapper;
        this.knowledgeDocMapper = knowledgeDocMapper;
        this.httpClient = new OkHttpClient.Builder()
                .connectTimeout(30, java.util.concurrent.TimeUnit.SECONDS)
                .readTimeout(60, java.util.concurrent.TimeUnit.SECONDS)
                .writeTimeout(30, java.util.concurrent.TimeUnit.SECONDS)
                .build();
    }

    /**
     * 创建新对话
     */
    @Transactional
    public AiConversation createConversation(Long userId, String userRole, String title) {
        AiConversation conversation = new AiConversation();
        conversation.setUserId(userId);
        conversation.setUserRole(userRole);
        conversation.setTitle(title != null ? title : "新对话");
        conversationMapper.insert(conversation);
        return conversation;
    }

    /**
     * 发送消息并获取 AI 回复
     */
    @Transactional
    public Map<String, Object> sendMessage(Long conversationId, String content) {
        // 1. 保存用户消息
        AiMessage userMessage = new AiMessage();
        userMessage.setConversationId(conversationId);
        userMessage.setRole("user");
        userMessage.setContent(content);
        messageMapper.insert(userMessage);

        // 2. 获取对话历史
        List<AiMessage> history = messageMapper.selectByConversationId(conversationId);

        // 3. RAG 增强：检索相关文档
        String knowledgeContext = retrieveKnowledge(content);

        // 4. 调用 LLM API
        String reply = callLlm(history, knowledgeContext);

        // 5. 保存 AI 回复
        AiMessage assistantMessage = new AiMessage();
        assistantMessage.setConversationId(conversationId);
        assistantMessage.setRole("assistant");
        assistantMessage.setContent(reply);
        messageMapper.insert(assistantMessage);

        // 6. 更新对话标题（如果是第一条消息）
        if (history.size() <= 1) {
            AiConversation conversation = conversationMapper.selectById(conversationId);
            if (conversation != null) {
                String title = content.length() > 50 ? content.substring(0, 50) + "..." : content;
                conversation.setTitle(title);
                conversationMapper.update(conversation);
            }
        }

        Map<String, Object> result = new HashMap<>();
        result.put("userMessage", userMessage);
        result.put("assistantMessage", assistantMessage);
        return result;
    }

    /**
     * 获取对话历史
     */
    public List<AiMessage> getHistory(Long conversationId) {
        return messageMapper.selectByConversationId(conversationId);
    }

    /**
     * 删除对话
     */
    @Transactional
    public void deleteConversation(Long id) {
        messageMapper.deleteByConversationId(id);
        conversationMapper.deleteById(id);
    }

    /**
     * 获取用户的对话列表
     */
    public List<AiConversation> getUserConversations(Long userId) {
        Map<String, Object> params = new HashMap<>();
        params.put("userId", userId);
        params.put("pageNo", 0);
        params.put("pageSize", 100);
        return conversationMapper.selectList(params);
    }

    /**
     * RAG 检索：根据用户问题搜索相关知识库文档
     */
    private String retrieveKnowledge(String query) {
        if (query == null || query.trim().isEmpty()) {
            return "";
        }
        try {
            List<AiKnowledgeDoc> docs = knowledgeDocMapper.searchByKeyword(query);
            if (docs.isEmpty()) {
                return "";
            }
            return docs.stream()
                    .limit(3)
                    .map(doc -> String.format("【%s】%s", doc.getTitle(), doc.getContent()))
                    .collect(Collectors.joining("\n\n"));
        } catch (Exception e) {
            return "";
        }
    }

    /**
     * 调用 LLM API
     */
    private String callLlm(List<AiMessage> history, String knowledgeContext) {
        // 如果 API Key 是占位符，返回模拟回复
        if (apiKey == null || apiKey.isEmpty() || "sk-placeholder".equals(apiKey)) {
            return getMockReply(history);
        }

        try {
            // 构建 messages 数组
            StringBuilder messagesJson = new StringBuilder();
            messagesJson.append("[");

            // System prompt
            String systemPrompt = SYSTEM_PROMPT;
            if (!knowledgeContext.isEmpty()) {
                systemPrompt += "\n\n以下是相关的校园知识库内容，请基于这些信息回答：\n" + knowledgeContext;
            }
            messagesJson.append(String.format("{\"role\":\"system\",\"content\":%s}",
                    escapeJson(systemPrompt)));

            // 历史消息（取最近 maxHistory 条）
            List<AiMessage> recentHistory = history.size() > maxHistory
                    ? history.subList(history.size() - maxHistory, history.size())
                    : history;

            for (AiMessage msg : recentHistory) {
                messagesJson.append(",");
                messagesJson.append(String.format("{\"role\":\"%s\",\"content\":%s}",
                        msg.getRole(), escapeJson(msg.getContent())));
            }

            messagesJson.append("]");

            // 构建请求体
            String requestBody = String.format(
                    "{\"model\":\"%s\",\"messages\":%s,\"temperature\":0.7,\"max_tokens\":2048}",
                    model, messagesJson.toString()
            );

            // 发送 HTTP 请求
            Request request = new Request.Builder()
                    .url(baseUrl + "/v1/chat/completions")
                    .addHeader("Authorization", "Bearer " + apiKey)
                    .addHeader("Content-Type", "application/json")
                    .post(RequestBody.create(requestBody, MediaType.parse("application/json")))
                    .build();

            try (Response response = httpClient.newCall(request).execute()) {
                if (!response.isSuccessful() || response.body() == null) {
                    return "抱歉，AI 服务暂时不可用，请稍后再试。";
                }
                String responseBody = response.body().string();
                return extractContentFromResponse(responseBody);
            }

        } catch (IOException e) {
            return "抱歉，网络连接失败，请检查网络后重试。";
        } catch (Exception e) {
            return "抱歉，处理请求时出现错误，请稍后再试。";
        }
    }

    /**
     * 从 LLM 响应中提取回复内容
     */
    private String extractContentFromResponse(String responseBody) {
        try {
            // 简单 JSON 解析：提取 choices[0].message.content
            com.alibaba.fastjson2.JSONObject json = com.alibaba.fastjson2.JSON.parseObject(responseBody);
            return json.getJSONArray("choices")
                    .getJSONObject(0)
                    .getJSONObject("message")
                    .getString("content");
        } catch (Exception e) {
            return "抱歉，解析 AI 回复时出现错误。";
        }
    }

    /**
     * 模拟回复（无 API Key 时使用）
     */
    private String getMockReply(List<AiMessage> history) {
        String lastQuestion = history.isEmpty() ? "" : history.get(history.size() - 1).getContent();

        if (lastQuestion.contains("考试") || lastQuestion.contains("成绩")) {
            return "关于考试相关问题，您可以查看教务管理中的考试安排和成绩查询模块。如需查看具体考试信息，请联系您的任课教师或教务处。";
        } else if (lastQuestion.contains("课程") || lastQuestion.contains("选课")) {
            return "您可以在课程中心查看所有课程信息，包括课程简介、授课教师、学分课时等。选课一般在每学期末进行，请关注教务处通知。";
        } else if (lastQuestion.contains("通知") || lastQuestion.contains("公告")) {
            return "最新校园公告已在公告栏发布，您可以前往校园公告页面查看。如有紧急通知，系统也会通过消息提醒您。";
        } else if (lastQuestion.contains("你好") || lastQuestion.contains("hello") || lastQuestion.contains("嗨")) {
            return "你好！我是智慧校园 AI 助教，很高兴为您服务！您可以问我关于课程、考试、通知、校园服务等方面的问题。";
        } else {
            return "感谢您的提问！我是智慧校园 AI 助教，可以帮您解答关于课程学习、考试安排、校园通知、教务管理等方面的问题。请详细描述您的需求，我会尽力提供帮助。";
        }
    }

    /**
     * JSON 字符串转义
     */
    private String escapeJson(String text) {
        if (text == null) return "\"\"";
        String escaped = text
                .replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
        return "\"" + escaped + "\"";
    }
}
