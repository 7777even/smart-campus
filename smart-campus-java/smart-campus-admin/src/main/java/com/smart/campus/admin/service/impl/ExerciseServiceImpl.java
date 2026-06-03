package com.smart.campus.admin.service.impl;

import com.campus.mappers.BaseMapper;
import com.campus.service.BaseService;
import com.smart.campus.admin.entity.Exercise;
import com.smart.campus.admin.mappers.ExerciseMapper;
import org.springframework.stereotype.Service;

/**
 * 习题 Service 实现
 */
@Service
public class ExerciseServiceImpl extends BaseService<Exercise> {

    private final ExerciseMapper exerciseMapper;

    public ExerciseServiceImpl(ExerciseMapper exerciseMapper) {
        this.exerciseMapper = exerciseMapper;
    }

    @Override
    protected BaseMapper<Exercise> getMapper() {
        return exerciseMapper;
    }
}
