package com.smart.campus.admin.service;

import com.campus.mappers.BaseMapper;
import com.campus.service.BaseService;
import com.smart.campus.admin.entity.AiKnowledgeDoc;
import com.smart.campus.admin.mappers.AiKnowledgeDocMapper;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * AI 知识库文档 Service
 */
@Service
public class AiKnowledgeService extends BaseService<AiKnowledgeDoc> {

    private final AiKnowledgeDocMapper knowledgeDocMapper;

    public AiKnowledgeService(AiKnowledgeDocMapper knowledgeDocMapper) {
        this.knowledgeDocMapper = knowledgeDocMapper;
    }

    @Override
    protected BaseMapper<AiKnowledgeDoc> getMapper() {
        return knowledgeDocMapper;
    }

    /**
     * 关键词搜索知识库文档
     */
    public List<AiKnowledgeDoc> search(String keyword) {
        return knowledgeDocMapper.searchByKeyword(keyword);
    }
}
