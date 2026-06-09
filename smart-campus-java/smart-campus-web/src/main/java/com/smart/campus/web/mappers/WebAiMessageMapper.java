package com.smart.campus.web.mappers;

import com.campus.entity.AiMessage;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * AI 对话消息 Mapper（学生端）
 */
public interface WebAiMessageMapper {

    List<AiMessage> selectByConversationId(@Param("conversationId") Long conversationId);

    int insert(AiMessage msg);
}
