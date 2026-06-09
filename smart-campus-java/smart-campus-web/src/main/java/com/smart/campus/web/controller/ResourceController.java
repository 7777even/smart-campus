package com.smart.campus.web.controller;

import com.campus.result.R;
import com.smart.campus.web.biz.ResourceWebBiz;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 资源控制器（学生端）
 */
@RestController
@RequestMapping("/resources")
@Validated
@Tag(name = "资源")
public class ResourceController {

    private final ResourceWebBiz resourceWebBiz;

    public ResourceController(ResourceWebBiz resourceWebBiz) {
        this.resourceWebBiz = resourceWebBiz;
    }

    @GetMapping("/by-course/{courseId}")
    @Operation(summary = "获取课程下的资源列表")
    public R<List<Map<String, Object>>> listByCourse(@PathVariable String courseId) {
        return R.ok(resourceWebBiz.listByCourse(courseId));
    }

    @GetMapping("/{id}")
    @Operation(summary = "资源详情")
    public R<Map<String, Object>> getDetail(@PathVariable String id) {
        return R.ok(resourceWebBiz.getDetail(id));
    }
}
