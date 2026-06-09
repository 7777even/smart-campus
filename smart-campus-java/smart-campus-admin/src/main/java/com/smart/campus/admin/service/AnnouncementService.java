package com.smart.campus.admin.service;

import com.campus.mappers.BaseMapper;
import com.campus.service.BaseService;
import com.campus.entity.Announcement;
import com.campus.mappers.AnnouncementMapper;
import org.springframework.stereotype.Service;

@Service
public class AnnouncementService extends BaseService<Announcement> {

    private final AnnouncementMapper announcementMapper;

    public AnnouncementService(AnnouncementMapper announcementMapper) {
        this.announcementMapper = announcementMapper;
    }

    @Override
    protected BaseMapper<Announcement> getMapper() {
        return announcementMapper;
    }
}
