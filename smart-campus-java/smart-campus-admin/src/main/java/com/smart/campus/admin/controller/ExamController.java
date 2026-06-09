package com.smart.campus.admin.controller;

import com.campus.entity.PageResult;
import com.campus.result.R;
import com.campus.entity.Exam;
import com.smart.campus.admin.service.impl.ExamServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 考试 Controller
 */
@RestController
@RequestMapping("/exams")
@Tag(name = "考试管理")
public class ExamController {

    private final ExamServiceImpl examService;

    public ExamController(ExamServiceImpl examService) {
        this.examService = examService;
    }

    @GetMapping("/page")
    @Operation(summary = "分页查询考试")
    public R<PageResult<Exam>> page(
            @RequestParam(defaultValue = "1") int pageNo,
            @RequestParam(defaultValue = "15") int pageSize,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate) {
        Map<String, Object> params = new HashMap<>();
        params.put("keyword", keyword);
        params.put("status", status);
        params.put("startDate", startDate);
        params.put("endDate", endDate);
        return R.ok(examService.page(pageNo, pageSize, params));
    }

    @GetMapping("/list")
    @Operation(summary = "查询考试列表")
    public R<List<Exam>> list(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate) {
        Map<String, Object> params = new HashMap<>();
        params.put("keyword", keyword);
        params.put("status", status);
        params.put("startDate", startDate);
        params.put("endDate", endDate);
        return R.ok(examService.list(params));
    }

    @GetMapping("/{id}")
    @Operation(summary = "根据ID查询考试")
    public R<Exam> getById(@PathVariable Long id) {
        return R.ok(examService.getById(id));
    }

    @PostMapping
    @Operation(summary = "新增考试")
    public R<Void> save(@RequestBody Exam exam) {
        examService.save(exam);
        return R.ok();
    }

    @PutMapping
    @Operation(summary = "修改考试")
    public R<Void> update(@RequestBody Exam exam) {
        examService.update(exam);
        return R.ok();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除考试")
    public R<Void> delete(@PathVariable Long id) {
        examService.delete(id);
        return R.ok();
    }

    @DeleteMapping("/batch")
    @Operation(summary = "批量删除考试")
    public R<Void> deleteBatch(@RequestBody Map<String, List<Long>> request) {
        examService.deleteBatch(request.get("ids"));
        return R.ok();
    }
}
