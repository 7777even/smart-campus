package com.smart.campus.admin.service;

import com.campus.entity.AiKnowledgeDoc;
import com.campus.mappers.AiKnowledgeDocMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * AI 知识库文档服务单元测试
 */
@ExtendWith(MockitoExtension.class)
class AiKnowledgeServiceTest {

    @Mock
    private AiKnowledgeDocMapper knowledgeDocMapper;

    @InjectMocks
    private AiKnowledgeService knowledgeService;

    @Test
    @DisplayName("search — 关键词搜索返回文档列表")
    void search() {
        AiKnowledgeDoc doc = new AiKnowledgeDoc();
        doc.setId(1L);
        doc.setTitle("学籍管理规定");
        doc.setCategory("管理规定");

        when(knowledgeDocMapper.searchByKeyword("学籍")).thenReturn(List.of(doc));

        List<AiKnowledgeDoc> results = knowledgeService.search("学籍");

        assertEquals(1, results.size());
        assertEquals("学籍管理规定", results.get(0).getTitle());
        verify(knowledgeDocMapper).searchByKeyword("学籍");
    }

    @Test
    @DisplayName("search — 无匹配时返回空列表")
    void searchNoResults() {
        when(knowledgeDocMapper.searchByKeyword("不存在的关键词")).thenReturn(List.of());
        assertTrue(knowledgeService.search("不存在的关键词").isEmpty());
    }

    @Test
    @DisplayName("search — 空关键词也应正常处理")
    void searchEmptyKeyword() {
        when(knowledgeDocMapper.searchByKeyword("")).thenReturn(List.of());
        assertTrue(knowledgeService.search("").isEmpty());
    }

    @Test
    @DisplayName("getMapper — 返回正确的 Mapper 实例")
    void getMapper() {
        assertNotNull(knowledgeService.getMapper());
        assertSame(knowledgeDocMapper, knowledgeService.getMapper());
    }
}
