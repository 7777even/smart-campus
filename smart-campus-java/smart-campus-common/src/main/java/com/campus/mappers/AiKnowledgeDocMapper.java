package com.campus.mappers;

import com.campus.mappers.BaseMapper;
import com.campus.entity.AiKnowledgeDoc;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * AI 知识库文档 Mapper
 */
public interface AiKnowledgeDocMapper extends BaseMapper<AiKnowledgeDoc> {

    @Override
    List<AiKnowledgeDoc> selectList(Map<String, Object> params);

    @Override
    long selectCount(Map<String, Object> params);

    List<AiKnowledgeDoc> searchByKeyword(@Param("keyword") String keyword);
}
