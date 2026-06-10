package com.smart.campus.admin.service;

import com.campus.entity.AiConversation;
import com.campus.entity.AiKnowledgeDoc;
import com.campus.entity.AiMessage;
import com.campus.mappers.AiConversationMapper;
import com.campus.mappers.AiKnowledgeDocMapper;
import com.campus.mappers.AiMessageMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * AI 对话服务单元测试
 * <p>
 * 测试核心逻辑：对话管理、Mock 回复、RAG 检索
 * LLM API 调用（OkHttp）的测试通过 Mock API Key 和 Mock 回复路径实现。
 */
@ExtendWith(MockitoExtension.class)
class AiChatServiceTest {

    @Mock
    private AiConversationMapper conversationMapper;

    @Mock
    private AiMessageMapper messageMapper;

    @Mock
    private AiKnowledgeDocMapper knowledgeDocMapper;

    @InjectMocks
    private AiChatService aiChatService;

    @Captor
    private ArgumentCaptor<AiConversation> conversationCaptor;

    @Captor
    private ArgumentCaptor<AiMessage> messageCaptor;

    @Test
    @DisplayName("createConversation — 创建新对话")
    void createConversation() {
        AiConversation result = aiChatService.createConversation(1L, "student", "测试对话");
        assertNotNull(result);
        assertEquals(1L, result.getUserId());
        assertEquals("student", result.getUserRole());
        assertEquals("测试对话", result.getTitle());
        verify(conversationMapper).insert(any(AiConversation.class));
    }

    @Test
    @DisplayName("createConversation — 无标题时使用默认标题")
    void createConversationDefaultTitle() {
        AiConversation result = aiChatService.createConversation(1L, "teacher", null);
        assertEquals("新对话", result.getTitle());
    }

    @Test
    @DisplayName("sendMessage — 发送消息并获取 Mock 回复")
    void sendMessage() {
        AiMessage existingMsg = new AiMessage();
        existingMsg.setRole("user");
        existingMsg.setContent("你好，有什么课程推荐？");
        existingMsg.setConversationId(1L);
        when(messageMapper.selectByConversationId(1L)).thenReturn(List.of(existingMsg));

        // 执行发送（API Key 默认 "sk-placeholder"，会走 mock 回复）
        Map<String, Object> result = aiChatService.sendMessage(1L, "你好，有什么课程推荐？");

        assertNotNull(result);
        assertTrue(result.containsKey("userMessage"));
        assertTrue(result.containsKey("assistantMessage"));

        // 验证用户消息保存
        AiMessage userMsg = (AiMessage) result.get("userMessage");
        assertEquals("user", userMsg.getRole());
        assertEquals("你好，有什么课程推荐？", userMsg.getContent());

        // 验证助手消息
        AiMessage assistantMsg = (AiMessage) result.get("assistantMessage");
        assertEquals("assistant", assistantMsg.getRole());
        assertNotNull(assistantMsg.getContent());
        assertFalse(assistantMsg.getContent().isEmpty());

        // 验证消息入库
        verify(messageMapper, times(2)).insert(any(AiMessage.class));
    }

    @Test
    @DisplayName("sendMessage — 关键词考试触发对应的 Mock 回复")
    void sendMessageExamKeyword() {
        AiMessage existingMsg = new AiMessage();
        existingMsg.setRole("user");
        existingMsg.setContent("我想查询考试成绩");
        existingMsg.setConversationId(1L);
        when(messageMapper.selectByConversationId(1L)).thenReturn(List.of(existingMsg));

        Map<String, Object> result = aiChatService.sendMessage(1L, "我想查询考试成绩");
        AiMessage assistantMsg = (AiMessage) result.get("assistantMessage");

        assertTrue(assistantMsg.getContent().contains("考试"));
    }

    @Test
    @DisplayName("sendMessage — 关键词课程触发对应的 Mock 回复")
    void sendMessageCourseKeyword() {
        AiMessage existingMsg = new AiMessage();
        existingMsg.setRole("user");
        existingMsg.setContent("我想选课");
        existingMsg.setConversationId(1L);
        when(messageMapper.selectByConversationId(1L)).thenReturn(List.of(existingMsg));

        Map<String, Object> result = aiChatService.sendMessage(1L, "我想选课");
        AiMessage assistantMsg = (AiMessage) result.get("assistantMessage");

        assertTrue(assistantMsg.getContent().contains("课程"));
    }

    @Test
    @DisplayName("sendMessage — 关键词通知触发对应的 Mock 回复")
    void sendMessageAnnouncementKeyword() {
        AiMessage existingMsg = new AiMessage();
        existingMsg.setRole("user");
        existingMsg.setContent("查看校园通知");
        existingMsg.setConversationId(1L);
        when(messageMapper.selectByConversationId(1L)).thenReturn(List.of(existingMsg));

        Map<String, Object> result = aiChatService.sendMessage(1L, "查看校园通知");
        AiMessage assistantMsg = (AiMessage) result.get("assistantMessage");

        assertTrue(assistantMsg.getContent().contains("公告"));
    }

    @Test
    @DisplayName("sendMessage — 问候语触发对应的 Mock 回复")
    void sendMessageGreetingKeyword() {
        AiMessage existingMsg = new AiMessage();
        existingMsg.setRole("user");
        existingMsg.setContent("你好！");
        existingMsg.setConversationId(1L);
        when(messageMapper.selectByConversationId(1L)).thenReturn(List.of(existingMsg));

        Map<String, Object> result = aiChatService.sendMessage(1L, "你好！");
        AiMessage assistantMsg = (AiMessage) result.get("assistantMessage");

        assertTrue(assistantMsg.getContent().contains("你好"));
    }

    @Test
    @DisplayName("sendMessage — 首条消息自动更新对话标题")
    void sendMessageUpdatesTitle() {
        AiConversation conversation = new AiConversation();
        conversation.setId(1L);
        conversation.setTitle("新对话");

        AiMessage existingMsg = new AiMessage();
        existingMsg.setRole("user");
        existingMsg.setContent("测试消息内容");
        existingMsg.setConversationId(1L);
        when(messageMapper.selectByConversationId(1L)).thenReturn(List.of(existingMsg));
        when(conversationMapper.selectById(1L)).thenReturn(conversation);

        aiChatService.sendMessage(1L, "测试消息内容");

        // 验证标题被更新
        verify(conversationMapper).update(conversationCaptor.capture());
        assertEquals("测试消息内容", conversationCaptor.getValue().getTitle());
    }

    @Test
    @DisplayName("sendMessage — 长消息标题自动截断")
    void sendMessageTruncatesLongTitle() {
        String longMsg = "a".repeat(100);
        AiConversation conversation = new AiConversation();
        conversation.setId(1L);
        conversation.setTitle("新对话");

        AiMessage existingMsg = new AiMessage();
        existingMsg.setRole("user");
        existingMsg.setContent(longMsg);
        existingMsg.setConversationId(1L);
        when(messageMapper.selectByConversationId(1L)).thenReturn(List.of(existingMsg));
        when(conversationMapper.selectById(1L)).thenReturn(conversation);

        aiChatService.sendMessage(1L, longMsg);

        verify(conversationMapper).update(conversationCaptor.capture());
        String updatedTitle = conversationCaptor.getValue().getTitle();
        // 50 字符 + "..."
        assertTrue(updatedTitle.endsWith("..."));
        assertEquals(53, updatedTitle.length());
    }

    @Test
    @DisplayName("sendMessage — 非首条消息不更新标题")
    void sendMessageDoesNotUpdateTitleForNonFirstMessage() {
        AiMessage existingMsg = new AiMessage();
        existingMsg.setRole("user");
        existingMsg.setContent("之前的消息");
        existingMsg.setConversationId(1L);

        when(messageMapper.selectByConversationId(1L)).thenReturn(List.of(existingMsg));

        aiChatService.sendMessage(1L, "新的消息");

        // 不应更新标题
        verify(conversationMapper, never()).update(any(AiConversation.class));
    }

    @Test
    @DisplayName("getHistory — 获取对话历史")
    void getHistory() {
        AiMessage msg = new AiMessage();
        msg.setRole("user");
        msg.setContent("你好");
        when(messageMapper.selectByConversationId(1L)).thenReturn(List.of(msg));

        List<AiMessage> history = aiChatService.getHistory(1L);

        assertEquals(1, history.size());
        assertEquals("你好", history.get(0).getContent());
    }

    @Test
    @DisplayName("deleteConversation — 删除对话及其消息")
    void deleteConversation() {
        aiChatService.deleteConversation(1L);
        verify(messageMapper).deleteByConversationId(1L);
        verify(conversationMapper).deleteById(1L);
    }

    @Test
    @DisplayName("getUserConversations — 获取用户对话列表")
    void getUserConversations() {
        AiConversation conv = new AiConversation();
        conv.setId(1L);
        conv.setTitle("测试对话");
        when(conversationMapper.selectList(anyMap())).thenReturn(List.of(conv));

        List<AiConversation> result = aiChatService.getUserConversations(1L);

        assertEquals(1, result.size());
        assertEquals("测试对话", result.get(0).getTitle());
        verify(conversationMapper).selectList(argThat(argMap -> {
            Object userId = argMap.get("userId");
            return userId != null && userId.equals(1L);
        }));
    }

    @Test
    @DisplayName("sendMessage — RAG 检索增强")
    void sendMessageWithRag() {
        AiKnowledgeDoc doc = new AiKnowledgeDoc();
        doc.setTitle("选课指南");
        doc.setContent("选课通常在每学期末进行...");
        when(knowledgeDocMapper.searchByKeyword("选课")).thenReturn(List.of(doc));
        when(messageMapper.selectByConversationId(1L)).thenReturn(List.of());

        Map<String, Object> result = aiChatService.sendMessage(1L, "如何选课？");
        AiMessage assistantMsg = (AiMessage) result.get("assistantMessage");
        assertNotNull(assistantMsg.getContent());

        // 验证知识库被检索
        verify(knowledgeDocMapper).searchByKeyword("如何选课？");
    }
}
