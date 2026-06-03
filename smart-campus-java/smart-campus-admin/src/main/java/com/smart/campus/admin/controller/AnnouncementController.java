package com.smart.campus.admin.controller;

import com.campus.result.R;
import com.smart.campus.admin.entity.Announcement;
import com.smart.campus.admin.service.AnnouncementService;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/announcements")
public class AnnouncementController {

    private final AnnouncementService announcementService;

    public AnnouncementController(AnnouncementService announcementService) {
        this.announcementService = announcementService;
    }

    /**
     * 新增公告
     */
    @PostMapping
    public R<Void> create(@RequestBody Announcement announcement) {
        announcementService.save(announcement);
        return R.ok();
    }

    /**
     * 分页查询公告列表
     */
    @GetMapping("/page")
    public R<?> page(
            @RequestParam(defaultValue = "1") int pageNo,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String level,
            @RequestParam(required = false) String status) {
        Map<String, Object> params = new HashMap<>();
        params.put("keyword", keyword);
        params.put("level", level);
        params.put("status", status);
        return R.ok(announcementService.page(pageNo, pageSize, params));
    }

    /**
     * 根据 ID 查询公告
     */
    @GetMapping("/{id}")
    public R<?> getById(@PathVariable Long id) {
        return R.ok(announcementService.getById(id));
    }

    /**
     * 更新公告
     */
    @PutMapping("/{id}")
    public R<Void> update(@PathVariable Long id, @RequestBody Announcement announcement) {
        announcement.setId(id);
        announcementService.update(announcement);
        return R.ok();
    }

    /**
     * 删除公告
     */
    @DeleteMapping("/{id}")
    public R<Void> delete(@PathVariable Long id) {
        announcementService.delete(id);
        return R.ok();
    }

    /**
     * 切换发布/草稿状态
     */
    @PutMapping("/{id}/toggle-publish")
    public R<?> togglePublish(@PathVariable Long id) {
        Announcement announcement = announcementService.getById(id);
        if (announcement == null) {
            return R.fail("公告不存在");
        }
        if ("已发布".equals(announcement.getStatus())) {
            announcement.setStatus("草稿");
        } else {
            announcement.setStatus("已发布");
        }
        announcementService.update(announcement);
        return R.ok(announcement);
    }
}
