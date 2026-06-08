package com.campus.service;

import com.campus.entity.PageResult;
import com.campus.mappers.BaseMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * BaseService 通用 CRUD 基类单元测试
 */
@ExtendWith(MockitoExtension.class)
class BaseServiceTest {

    @Mock
    private BaseMapper<String> mapper;

    private BaseService<String> service;

    @BeforeEach
    void setUp() {
        service = new BaseService<>() {
            @Override
            protected BaseMapper<String> getMapper() {
                return mapper;
            }
        };
    }

    @Test
    @DisplayName("getById — 委托给 mapper.selectById")
    void getById() {
        when(mapper.selectById(1L)).thenReturn("test");
        assertEquals("test", service.getById(1L));
        verify(mapper).selectById(1L);
    }

    @Test
    @DisplayName("getById — 不存在返回 null")
    void getByIdNotFound() {
        when(mapper.selectById(99L)).thenReturn(null);
        assertNull(service.getById(99L));
    }

    @Test
    @DisplayName("page — 正确计算分页参数并返回 PageResult")
    void page() {
        List<String> mockList = List.of("a", "b", "c");
        when(mapper.selectList(anyMap())).thenReturn(mockList);
        when(mapper.selectCount(anyMap())).thenReturn(25L);

        PageResult<String> result = service.page(2, 10, new HashMap<>(Map.of("status", "active")));

        assertEquals(25, result.getTotalCount());
        assertEquals(10, result.getPageSize());
        assertEquals(2, result.getPageNo());
        assertEquals(3, result.getPageTotal()); // ceil(25/10) = 3
        assertEquals(3, result.getList().size());

        // 验证 offset 计算：(2-1)*10 = 10
        verify(mapper).selectList(argThat(params ->
                params.containsKey("pageNo") && params.get("pageNo").equals(10)));
    }

    @Test
    @DisplayName("page — params 为 null 时创建新 Map")
    void pageWithNullParams() {
        when(mapper.selectList(anyMap())).thenReturn(List.of());
        when(mapper.selectCount(anyMap())).thenReturn(0L);

        PageResult<String> result = service.page(1, 15, null);

        assertEquals(0, result.getTotalCount());
        assertEquals(0, result.getPageTotal());
        assertTrue(result.getList().isEmpty());
    }

    @Test
    @DisplayName("list — 委托给 mapper.selectList")
    void list() {
        List<String> mockList = List.of("x", "y");
        when(mapper.selectList(anyMap())).thenReturn(mockList);

        List<String> result = service.list(Map.of("key", "value"));
        assertEquals(2, result.size());
        verify(mapper).selectList(Map.of("key", "value"));
    }

    @Test
    @DisplayName("save — 委托给 mapper.insert，返回影响行数")
    void save() {
        when(mapper.insert("entity")).thenReturn(1);
        int rows = service.save("entity");
        assertEquals(1, rows);
        verify(mapper).insert("entity");
    }

    @Test
    @DisplayName("update — 委托给 mapper.update")
    void update() {
        when(mapper.update("entity")).thenReturn(1);
        assertEquals(1, service.update("entity"));
    }

    @Test
    @DisplayName("delete — 委托给 mapper.deleteById")
    void delete() {
        when(mapper.deleteById(1L)).thenReturn(1);
        assertEquals(1, service.delete(1L));
    }

    @Test
    @DisplayName("deleteBatch — 委托给 mapper.deleteByIds")
    void deleteBatch() {
        List<Long> ids = List.of(1L, 2L, 3L);
        when(mapper.deleteByIds(ids)).thenReturn(3);
        assertEquals(3, service.deleteBatch(ids));
    }
}
