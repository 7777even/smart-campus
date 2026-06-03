package com.smart.campus.admin.service.impl;

import com.campus.mappers.BaseMapper;
import com.campus.service.BaseService;
import com.smart.campus.admin.entity.Course;
import com.smart.campus.admin.mappers.CourseMapper;
import org.springframework.stereotype.Service;

/**
 * 课程 Service 实现
 */
@Service
public class CourseServiceImpl extends BaseService<Course> {

    private final CourseMapper courseMapper;

    public CourseServiceImpl(CourseMapper courseMapper) {
        this.courseMapper = courseMapper;
    }

    @Override
    protected BaseMapper<Course> getMapper() {
        return courseMapper;
    }
}
