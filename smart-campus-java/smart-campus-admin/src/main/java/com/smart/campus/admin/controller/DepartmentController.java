package com.smart.campus.admin.controller;

import com.campus.entity.Department;
import com.campus.entity.PageResult;
import com.campus.result.R;
import com.campus.service.BaseService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 院系管理接口
 */
@Tag(name = "院系管理")
@RestController
@RequestMapping("/departments")
public class DepartmentController {

    private final BaseService<Department> departmentService;

    public DepartmentController(BaseService<Department> departmentService) {
        this.departmentService = departmentService;
    }

    @GetMapping("/page")
    @Operation(summary = "分页查询院系列表")
    public R<PageResult<Department>> page(
            @RequestParam(defaultValue = "1") int pageNo,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam Map<String, Object> params) {
        PageResult<Department> pageResult = departmentService.page(pageNo, pageSize, params);
        return R.ok(pageResult);
    }

    @GetMapping("/{id}")
    @Operation(summary = "根据ID查询院系")
    public R<Department> getById(@PathVariable Long id) {
        Department department = departmentService.getById(id);
        return R.ok(department);
    }

    @PostMapping
    @Operation(summary = "新增院系")
    public R<Void> create(@RequestBody Department department) {
        departmentService.save(department);
        return R.ok();
    }

    @PutMapping
    @Operation(summary = "修改院系")
    public R<Void> update(@RequestBody Department department) {
        departmentService.update(department);
        return R.ok();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除院系")
    public R<Void> delete(@PathVariable Long id) {
        departmentService.delete(id);
        return R.ok();
    }

    @DeleteMapping("/batch")
    @Operation(summary = "批量删除院系")
    public R<Void> deleteBatch(@RequestBody Map<String, List<Long>> request) {
        departmentService.deleteBatch(request.get("ids"));
        return R.ok();
    }
}
