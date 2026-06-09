package com.smart.campus.web.mappers;

import com.campus.mappers.BaseMapper;
import com.campus.entity.AiConversation;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * AI 对话记录 Mapper（学生端）
 */
public interface WebAiConversationMapper extends BaseMapper<AiConversation> {

    AiConversation selectByUserIdAndUserRole(@Param("userId") Long userId,
                                              @Param("userRole") String userRole);

    int insert(AiConversation conv);

    List<AiConversation> selectByUserId(@Param("userId") Long userId);

    int deleteById(@Param("id") Long id);
}
