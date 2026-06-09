package com.smart.campus.web.biz;

import com.campus.entity.PageResult;
import com.campus.entity.Announcement;
import com.smart.campus.web.mappers.WebAnnouncementMapper;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * 公告业务（学生端）
 */
@Component
public class AnnouncementWebBiz {

    private final WebAnnouncementMapper announcementMapper;

    public AnnouncementWebBiz(WebAnnouncementMapper announcementMapper) {
        this.announcementMapper = announcementMapper;
    }

    /**
     * 已发布公告分页列表
     */
    public PageResult<Map<String, Object>> pageList(int pageNo, int pageSize, Map<String, Object> params) {
        String keyword = (String) params.get("keyword");
        if (pageNo < 1) pageNo = 1;
        if (pageSize < 1) pageSize = 20;

        long total = announcementMapper.selectPublishedCount(keyword);
        List<Announcement> list = announcementMapper.selectPublishedList(keyword, pageNo, pageSize);

        List<Map<String, Object>> result = new ArrayList<>();
        for (Announcement a : list) {
            Map<String, Object> m = new HashMap<>();
            m.put("id", String.valueOf(a.getId()));
            m.put("title", a.getTitle());
            m.put("content", a.getContent());
            m.put("publisher", a.getPublisher());
            m.put("level", a.getLevel());
            m.put("status", a.getStatus());
            m.put("createTime", a.getCreateTime());
            result.add(m);
        }

        return new PageResult<>(total, pageSize, pageNo, result);
    }

    /**
     * 公告详情
     */
    public Map<String, Object> getDetail(String id) {
        Announcement announcement = announcementMapper.selectById(Long.valueOf(id));
        if (announcement == null) {
            throw new RuntimeException("公告不存在");
        }
        Map<String, Object> m = new HashMap<>();
        m.put("id", String.valueOf(announcement.getId()));
        m.put("title", announcement.getTitle());
        m.put("content", announcement.getContent());
        m.put("publisher", announcement.getPublisher());
        m.put("level", announcement.getLevel());
        m.put("status", announcement.getStatus());
        m.put("createTime", announcement.getCreateTime());
        return m;
    }
}
