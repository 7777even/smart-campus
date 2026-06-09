package com.smart.campus.web.controller;

import com.campus.entity.PageResult;
import com.campus.result.R;
import com.smart.campus.web.biz.AnnouncementWebBiz;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 公告控制器（学生端）
 */
@RestController
@RequestMapping("/announcements")
@Validated
@Tag(name = "公告")
public class AnnouncementController {

    private final AnnouncementWebBiz announcementWebBiz;

    public AnnouncementController(AnnouncementWebBiz announcementWebBiz) {
        this.announcementWebBiz = announcementWebBiz;
    }

    @GetMapping("/page")
    @Operation(summary = "分页查询已发布公告")
    public R<PageResult<Map<String, Object>>> pageList(
            @RequestParam(defaultValue = "1") int pageNo,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) String keyword) {
        Map<String, Object> params = new LinkedHashMap<>();
        params.put("keyword", keyword);
        return R.ok(announcementWebBiz.pageList(pageNo, pageSize, params));
    }

    @GetMapping("/{id}")
    @Operation(summary = "公告详情")
    public R<Map<String, Object>> getDetail(@PathVariable String id) {
        return R.ok(announcementWebBiz.getDetail(id));
    }
}
