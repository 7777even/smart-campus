package com.smart.campus.admin.mappers;

import com.campus.mappers.BaseMapper;
import com.smart.campus.admin.entity.Clazz;

import java.util.List;
import java.util.Map;

public interface ClazzMapper extends BaseMapper<Clazz> {

    @Override
    List<Clazz> selectList(Map<String, Object> params);

    @Override
    long selectCount(Map<String, Object> params);
}
