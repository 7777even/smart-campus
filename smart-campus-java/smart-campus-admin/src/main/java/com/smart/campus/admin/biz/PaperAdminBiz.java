package com.smart.campus.admin.biz;

import com.campus.entity.Paper;
import com.campus.exception.BusinessException;
import com.campus.service.BaseService;
import org.springframework.stereotype.Component;

/**
 * 试卷业务（管理端）
 */
@Component
public class PaperAdminBiz {

    private final BaseService<Paper> paperService;

    public PaperAdminBiz(BaseService<Paper> paperService) {
        this.paperService = paperService;
    }

    /**
     * 发布试卷
     */
    public void publish(Long id) {
        Paper paper = paperService.getById(id);
        if (paper == null) {
            throw new BusinessException("试卷不存在");
        }
        paper.setStatus("已发布");
        paperService.update(paper);
    }
}
