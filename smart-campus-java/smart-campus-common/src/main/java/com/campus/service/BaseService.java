package com.campus.service;

import com.campus.entity.PageResult;
import com.campus.mappers.BaseMapper;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 通用 Service 实现
 */
public abstract class BaseService<T> {

    protected abstract BaseMapper<T> getMapper();

    public T getById(Long id) {
        return getMapper().selectById(id);
    }

    public PageResult<T> page(int pageNo, int pageSize, Map<String, Object> params) {
        if (params == null) {
            params = new HashMap<>();
        }
        params.put("pageNo", (pageNo - 1) * pageSize);
        params.put("pageSize", pageSize);

        List<T> list = getMapper().selectList(params);
        long total = getMapper().selectCount(params);
        return new PageResult<>(total, pageSize, pageNo, list);
    }

    public List<T> list(Map<String, Object> params) {
        return getMapper().selectList(params);
    }

    public int save(T entity) {
        return getMapper().insert(entity);
    }

    public int update(T entity) {
        return getMapper().update(entity);
    }

    public int delete(Long id) {
        return getMapper().deleteById(id);
    }

    public int deleteBatch(List<Long> ids) {
        return getMapper().deleteByIds(ids);
    }
}
