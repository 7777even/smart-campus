package com.campus.mappers;

import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 通用 Mapper 接口 — 所有业务 Mapper 继承此接口
 */
public interface BaseMapper<T> {

    /**
     * 根据 ID 查询
     */
    T selectById(@Param("id") Long id);

    /**
     * 查询列表（带分页和条件）
     */
    List<T> selectList(Map<String, Object> params);

    /**
     * 查询总数
     */
    long selectCount(Map<String, Object> params);

    /**
     * 新增
     */
    int insert(T entity);

    /**
     * 更新
     */
    int update(T entity);

    /**
     * 根据 ID 删除
     */
    int deleteById(@Param("id") Long id);

    /**
     * 批量删除
     */
    int deleteByIds(@Param("ids") List<Long> ids);
}
