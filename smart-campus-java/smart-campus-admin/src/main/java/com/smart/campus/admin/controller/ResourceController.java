package com.smart.campus.admin.controller;

import com.campus.entity.PageResult;
import com.campus.entity.Resource;
import com.campus.result.R;
import com.campus.service.BaseService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import com.smart.campus.admin.biz.ResourceAdminBiz;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 资源控制器（管理端）
 */
@Tag(name = "资源管理")
@RestController
@RequestMapping("/resources")
public class ResourceController {

    private final BaseService<Resource> resourceService;
    private final ResourceAdminBiz resourceAdminBiz;

    public ResourceController(BaseService<Resource> resourceService, ResourceAdminBiz resourceAdminBiz) {
        this.resourceService = resourceService;
        this.resourceAdminBiz = resourceAdminBiz;
    }

    @GetMapping("/page")
    @Operation(summary = "分页查询资源")
    public R<PageResult<Resource>> page(
            @RequestParam(defaultValue = "1") int pageNo,
            @RequestParam(defaultValue = "15") int pageSize,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String category) {
        Map<String, Object> params = new HashMap<>();
        params.put("keyword", keyword);
        params.put("type", type);
        params.put("category", category);
        return R.ok(resourceService.page(pageNo, pageSize, params));
    }

    @GetMapping("/list")
    @Operation(summary = "查询资源列表")
    public R<List<Resource>> list(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String category) {
        Map<String, Object> params = new HashMap<>();
        params.put("keyword", keyword);
        params.put("type", type);
        params.put("category", category);
        return R.ok(resourceService.list(params));
    }

    @GetMapping("/{id}")
    @Operation(summary = "根据ID查询资源")
    public R<Resource> getById(@PathVariable Long id) {
        return R.ok(resourceService.getById(id));
    }

    @PostMapping
    @Operation(summary = "新增资源")
    public R<Void> save(@RequestBody Resource resource) {
        resourceService.save(resource);
        return R.ok();
    }

    @PutMapping
    @Operation(summary = "修改资源")
    public R<Void> update(@RequestBody Resource resource) {
        resourceService.update(resource);
        return R.ok();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除资源")
    public R<Void> delete(@PathVariable Long id) {
        resourceService.delete(id);
        return R.ok();
    }

    @DeleteMapping("/batch")
    @Operation(summary = "批量删除资源")
    public R<Void> deleteBatch(@RequestBody Map<String, List<Long>> request) {
        resourceService.deleteBatch(request.get("ids"));
        return R.ok();
    }

    @PostMapping("/upload")
    @Operation(summary = "上传资源文件")
    public R<Resource> upload(
            @RequestParam("file") org.springframework.web.multipart.MultipartFile file,
            @RequestParam(value = "name", required = false) String name,
            @RequestParam(value = "type", required = false, defaultValue = "其他") String type,
            @RequestParam(value = "category", required = false, defaultValue = "课程资料") String category,
            @RequestParam(value = "description", required = false) String description) {
        Resource resource = resourceAdminBiz.buildResource(file, name, type, category, description);
        resourceService.save(resource);
        return R.ok(resource);
    }
}
