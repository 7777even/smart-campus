package com.smart.campus.admin.controller;

import com.campus.entity.PageResult;
import com.campus.result.R;
import com.campus.entity.Clazz;
import com.smart.campus.admin.service.impl.ClazzServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 班级管理接口
 */
@RestController
@RequestMapping("/classes")
@Tag(name = "班级管理")
public class ClazzController {

    private final ClazzServiceImpl clazzService;

    @Autowired
    public ClazzController(ClazzServiceImpl clazzService) {
        this.clazzService = clazzService;
    }

    @GetMapping("/page")
    @Operation(summary = "分页查询班级列表")
    public R<PageResult<Clazz>> page(
            @RequestParam(defaultValue = "1") int pageNo,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam Map<String, Object> params) {
        PageResult<Clazz> pageResult = clazzService.page(pageNo, pageSize, params);
        return R.ok(pageResult);
    }

    @GetMapping("/{id}")
    @Operation(summary = "根据ID查询班级")
    public R<Clazz> getById(@PathVariable Long id) {
        Clazz clazz = clazzService.getById(id);
        return R.ok(clazz);
    }

    @PostMapping
    @Operation(summary = "新增班级")
    public R<Void> create(@RequestBody Clazz clazz) {
        clazzService.save(clazz);
        return R.ok();
    }

    @PutMapping
    @Operation(summary = "修改班级")
    public R<Void> update(@RequestBody Clazz clazz) {
        clazzService.update(clazz);
        return R.ok();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除班级")
    public R<Void> delete(@PathVariable Long id) {
        clazzService.delete(id);
        return R.ok();
    }

    @DeleteMapping("/batch")
    @Operation(summary = "批量删除班级")
    public R<Void> deleteBatch(@RequestBody Map<String, List<Long>> request) {
        clazzService.deleteBatch(request.get("ids"));
        return R.ok();
    }
}
