package com.smart.campus.admin.controller;

import com.campus.entity.Major;
import com.campus.entity.PageResult;
import com.campus.result.R;
import com.campus.service.BaseService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 专业管理接口
 */
@Tag(name = "专业管理")
@RestController
@RequestMapping("/majors")
public class MajorController {

    private final BaseService<Major> majorService;

    public MajorController(BaseService<Major> majorService) {
        this.majorService = majorService;
    }

    @GetMapping("/page")
    @Operation(summary = "分页查询专业列表")
    public R<PageResult<Major>> page(
            @RequestParam(defaultValue = "1") int pageNo,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam Map<String, Object> params) {
        PageResult<Major> pageResult = majorService.page(pageNo, pageSize, params);
        return R.ok(pageResult);
    }

    @GetMapping("/{id}")
    @Operation(summary = "根据ID查询专业")
    public R<Major> getById(@PathVariable Long id) {
        Major major = majorService.getById(id);
        return R.ok(major);
    }

    @PostMapping
    @Operation(summary = "新增专业")
    public R<Void> create(@RequestBody Major major) {
        majorService.save(major);
        return R.ok();
    }

    @PutMapping
    @Operation(summary = "修改专业")
    public R<Void> update(@RequestBody Major major) {
        majorService.update(major);
        return R.ok();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除专业")
    public R<Void> delete(@PathVariable Long id) {
        majorService.delete(id);
        return R.ok();
    }

    @DeleteMapping("/batch")
    @Operation(summary = "批量删除专业")
    public R<Void> deleteBatch(@RequestBody Map<String, List<Long>> request) {
        majorService.deleteBatch(request.get("ids"));
        return R.ok();
    }
}
