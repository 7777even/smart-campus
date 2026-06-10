package com.smart.campus.admin.controller;

import com.campus.entity.PageResult;
import com.campus.result.R;
import com.campus.entity.Announcement;
import com.smart.campus.admin.biz.AnnouncementAdminBiz;
import com.smart.campus.admin.service.AnnouncementService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 公告控制器（管理端）
 */
@Tag(name = "公告管理")
@RestController
@RequestMapping("/announcements")
public class AnnouncementController {

    private final AnnouncementService announcementService;
    private final AnnouncementAdminBiz announcementAdminBiz;

    public AnnouncementController(AnnouncementService announcementService,
                                  AnnouncementAdminBiz announcementAdminBiz) {
        this.announcementService = announcementService;
        this.announcementAdminBiz = announcementAdminBiz;
    }

    /**
     * 新增公告
     */
    @PostMapping
    @Operation(summary = "新增公告")
    public R<Void> create(@RequestBody Announcement announcement) {
        announcementService.save(announcement);
        return R.ok();
    }

    /**
     * 分页查询公告列表
     */
    @GetMapping("/page")
    @Operation(summary = "分页查询公告列表")
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
    @Operation(summary = "根据ID查询公告")
    public R<?> getById(@PathVariable Long id) {
        return R.ok(announcementService.getById(id));
    }

    /**
     * 更新公告
     */
    @PutMapping("/{id}")
    @Operation(summary = "更新公告")
    public R<Void> update(@PathVariable Long id, @RequestBody Announcement announcement) {
        announcement.setId(id);
        announcementService.update(announcement);
        return R.ok();
    }

    /**
     * 删除公告
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "删除公告")
    public R<Void> delete(@PathVariable Long id) {
        announcementService.delete(id);
        return R.ok();
    }

    /**
     * 切换发布/草稿状态
     */
    @PutMapping("/{id}/toggle-publish")
    @Operation(summary = "切换发布状态")
    public R<?> togglePublish(@PathVariable Long id) {
        return R.ok(announcementAdminBiz.togglePublish(id));
    }
}
