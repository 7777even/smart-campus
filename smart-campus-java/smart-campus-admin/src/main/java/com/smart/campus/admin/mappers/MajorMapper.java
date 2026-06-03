package com.smart.campus.admin.mappers;

import com.campus.mappers.BaseMapper;
import com.smart.campus.admin.entity.Major;

import java.util.List;
import java.util.Map;

public interface MajorMapper extends BaseMapper<Major> {

    @Override
    List<Major> selectList(Map<String, Object> params);

    @Override
    long selectCount(Map<String, Object> params);
}
