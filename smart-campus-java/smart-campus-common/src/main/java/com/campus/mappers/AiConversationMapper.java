package com.campus.mappers;

import com.campus.mappers.BaseMapper;
import com.campus.entity.AiConversation;

import java.util.List;
import java.util.Map;

/**
 * AI 对话记录 Mapper
 */
public interface AiConversationMapper extends BaseMapper<AiConversation> {

    @Override
    List<AiConversation> selectList(Map<String, Object> params);

    @Override
    long selectCount(Map<String, Object> params);
}
