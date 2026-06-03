package com.smart.campus.admin.controller;

import com.campus.entity.PageResult;
import com.campus.result.R;
import com.smart.campus.admin.entity.Teacher;
import com.smart.campus.admin.service.impl.TeacherServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 教师管理接口
 */
@RestController
@RequestMapping("/teachers")
@Tag(name = "教师管理")
public class TeacherController {

    private final TeacherServiceImpl teacherService;

    @Autowired
    public TeacherController(TeacherServiceImpl teacherService) {
        this.teacherService = teacherService;
    }

    @GetMapping("/page")
    @Operation(summary = "分页查询教师列表")
    public R<PageResult<Teacher>> page(
            @RequestParam(defaultValue = "1") int pageNo,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam Map<String, Object> params) {
        PageResult<Teacher> pageResult = teacherService.page(pageNo, pageSize, params);
        return R.ok(pageResult);
    }

    @GetMapping("/{id}")
    @Operation(summary = "根据ID查询教师")
    public R<Teacher> getById(@PathVariable Long id) {
        Teacher teacher = teacherService.getById(id);
        return R.ok(teacher);
    }

    @PostMapping
    @Operation(summary = "新增教师")
    public R<Void> create(@RequestBody Teacher teacher) {
        teacherService.save(teacher);
        return R.ok();
    }

    @PutMapping
    @Operation(summary = "修改教师")
    public R<Void> update(@RequestBody Teacher teacher) {
        teacherService.update(teacher);
        return R.ok();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除教师")
    public R<Void> delete(@PathVariable Long id) {
        teacherService.delete(id);
        return R.ok();
    }

    @DeleteMapping("/batch")
    @Operation(summary = "批量删除教师")
    public R<Void> deleteBatch(@RequestBody Map<String, List<Long>> request) {
        teacherService.deleteBatch(request.get("ids"));
        return R.ok();
    }
}
