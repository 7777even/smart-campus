package com.smart.campus.admin.controller;

import com.campus.entity.PageResult;
import com.campus.result.R;
import com.campus.entity.Course;
import com.smart.campus.admin.service.impl.CourseServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 课程 Controller
 */
@RestController
@RequestMapping("/courses")
@Tag(name = "课程管理")
public class CourseController {

    private final CourseServiceImpl courseService;

    public CourseController(CourseServiceImpl courseService) {
        this.courseService = courseService;
    }

    @GetMapping("/page")
    @Operation(summary = "分页查询课程")
    public R<PageResult<Course>> page(
            @RequestParam(defaultValue = "1") int pageNo,
            @RequestParam(defaultValue = "15") int pageSize,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Long departmentId,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) Integer credit) {
        Map<String, Object> params = new HashMap<>();
        params.put("keyword", keyword);
        params.put("departmentId", departmentId);
        params.put("type", type);
        params.put("credit", credit);
        return R.ok(courseService.page(pageNo, pageSize, params));
    }

    @GetMapping("/list")
    @Operation(summary = "查询课程列表")
    public R<List<Course>> list(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Long departmentId,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) Integer credit) {
        Map<String, Object> params = new HashMap<>();
        params.put("keyword", keyword);
        params.put("departmentId", departmentId);
        params.put("type", type);
        params.put("credit", credit);
        return R.ok(courseService.list(params));
    }

    @GetMapping("/{id}")
    @Operation(summary = "根据ID查询课程")
    public R<Course> getById(@PathVariable Long id) {
        return R.ok(courseService.getById(id));
    }

    @PostMapping
    @Operation(summary = "新增课程")
    public R<Void> save(@RequestBody Course course) {
        courseService.save(course);
        return R.ok();
    }

    @PutMapping
    @Operation(summary = "修改课程")
    public R<Void> update(@RequestBody Course course) {
        courseService.update(course);
        return R.ok();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除课程")
    public R<Void> delete(@PathVariable Long id) {
        courseService.delete(id);
        return R.ok();
    }

    @DeleteMapping("/batch")
    @Operation(summary = "批量删除课程")
    public R<Void> deleteBatch(@RequestBody Map<String, List<Long>> request) {
        courseService.deleteBatch(request.get("ids"));
        return R.ok();
    }
}
