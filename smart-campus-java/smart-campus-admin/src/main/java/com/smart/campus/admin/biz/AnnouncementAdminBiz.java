package com.smart.campus.admin.biz;

import com.campus.exception.BusinessException;
import com.campus.entity.Announcement;
import com.smart.campus.admin.service.AnnouncementService;
import org.springframework.stereotype.Component;

/**
 * 公告业务（管理端）
 */
@Component
public class AnnouncementAdminBiz {

    private final AnnouncementService announcementService;

    public AnnouncementAdminBiz(AnnouncementService announcementService) {
        this.announcementService = announcementService;
    }

    /**
     * 切换发布/草稿状态
     */
    public Announcement togglePublish(Long id) {
        Announcement announcement = announcementService.getById(id);
        if (announcement == null) {
            throw new BusinessException("公告不存在");
        }
        String newStatus = "已发布".equals(announcement.getStatus()) ? "草稿" : "已发布";
        announcement.setStatus(newStatus);
        announcementService.update(announcement);
        return announcement;
    }
}
