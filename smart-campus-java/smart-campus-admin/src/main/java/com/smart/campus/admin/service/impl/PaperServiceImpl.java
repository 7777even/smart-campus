package com.smart.campus.admin.service.impl;

import com.campus.mappers.BaseMapper;
import com.campus.service.BaseService;
import com.smart.campus.admin.entity.Paper;
import com.smart.campus.admin.mappers.PaperMapper;
import org.springframework.stereotype.Service;

/**
 * 试卷 Service 实现
 */
@Service
public class PaperServiceImpl extends BaseService<Paper> {

    private final PaperMapper paperMapper;

    public PaperServiceImpl(PaperMapper paperMapper) {
        this.paperMapper = paperMapper;
    }

    @Override
    protected BaseMapper<Paper> getMapper() {
        return paperMapper;
    }
}
