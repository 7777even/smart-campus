package com.smart.campus.admin.controller;

import com.campus.entity.PageResult;
import com.campus.result.R;
import com.campus.entity.Student;
import com.smart.campus.admin.service.impl.StudentServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 学生管理接口
 */
@RestController
@RequestMapping("/students")
@Tag(name = "学生管理")
public class StudentController {

    private final StudentServiceImpl studentService;

    @Autowired
    public StudentController(StudentServiceImpl studentService) {
        this.studentService = studentService;
    }

    @GetMapping("/page")
    @Operation(summary = "分页查询学生列表")
    public R<PageResult<Student>> page(
            @RequestParam(defaultValue = "1") int pageNo,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam Map<String, Object> params) {
        PageResult<Student> pageResult = studentService.page(pageNo, pageSize, params);
        return R.ok(pageResult);
    }

    @GetMapping("/{id}")
    @Operation(summary = "根据ID查询学生")
    public R<Student> getById(@PathVariable Long id) {
        Student student = studentService.getById(id);
        return R.ok(student);
    }

    @PostMapping
    @Operation(summary = "新增学生")
    public R<Void> create(@RequestBody Student student) {
        studentService.save(student);
        return R.ok();
    }

    @PutMapping
    @Operation(summary = "修改学生")
    public R<Void> update(@RequestBody Student student) {
        studentService.update(student);
        return R.ok();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除学生")
    public R<Void> delete(@PathVariable Long id) {
        studentService.delete(id);
        return R.ok();
    }

    @DeleteMapping("/batch")
    @Operation(summary = "批量删除学生")
    public R<Void> deleteBatch(@RequestBody Map<String, List<Long>> request) {
        studentService.deleteBatch(request.get("ids"));
        return R.ok();
    }
}
