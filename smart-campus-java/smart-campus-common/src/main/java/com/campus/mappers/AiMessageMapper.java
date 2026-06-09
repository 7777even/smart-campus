package com.campus.mappers;

import com.campus.entity.AiMessage;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * AI 对话消息 Mapper
 */
public interface AiMessageMapper {

    List<AiMessage> selectByConversationId(@Param("conversationId") Long conversationId);

    int insert(AiMessage message);

    int deleteByConversationId(@Param("conversationId") Long conversationId);
}
