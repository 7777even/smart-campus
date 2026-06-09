package com.campus.mappers;

import com.campus.mappers.BaseMapper;
import com.campus.entity.Major;

import java.util.List;
import java.util.Map;

public interface MajorMapper extends BaseMapper<Major> {

    @Override
    List<Major> selectList(Map<String, Object> params);

    @Override
    long selectCount(Map<String, Object> params);
}
