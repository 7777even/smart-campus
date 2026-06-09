package com.smart.campus.admin.controller;

import com.campus.entity.PageResult;
import com.campus.result.R;
import com.campus.entity.Paper;
import com.smart.campus.admin.service.impl.PaperServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 试卷 Controller
 */
@RestController
@RequestMapping("/papers")
@Tag(name = "试卷管理")
public class PaperController {

    private final PaperServiceImpl paperService;

    public PaperController(PaperServiceImpl paperService) {
        this.paperService = paperService;
    }

    @GetMapping("/page")
    @Operation(summary = "分页查询试卷")
    public R<PageResult<Paper>> page(
            @RequestParam(defaultValue = "1") int pageNo,
            @RequestParam(defaultValue = "15") int pageSize,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Long courseId,
            @RequestParam(required = false) String status) {
        Map<String, Object> params = new HashMap<>();
        params.put("keyword", keyword);
        params.put("courseId", courseId);
        params.put("status", status);
        return R.ok(paperService.page(pageNo, pageSize, params));
    }

    @GetMapping("/list")
    @Operation(summary = "查询试卷列表")
    public R<List<Paper>> list(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Long courseId,
            @RequestParam(required = false) String status) {
        Map<String, Object> params = new HashMap<>();
        params.put("keyword", keyword);
        params.put("courseId", courseId);
        params.put("status", status);
        return R.ok(paperService.list(params));
    }

    @GetMapping("/{id}")
    @Operation(summary = "根据ID查询试卷")
    public R<Paper> getById(@PathVariable Long id) {
        return R.ok(paperService.getById(id));
    }

    @PostMapping
    @Operation(summary = "新增试卷")
    public R<Void> save(@RequestBody Paper paper) {
        paperService.save(paper);
        return R.ok();
    }

    @PutMapping
    @Operation(summary = "修改试卷")
    public R<Void> update(@RequestBody Paper paper) {
        paperService.update(paper);
        return R.ok();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除试卷")
    public R<Void> delete(@PathVariable Long id) {
        paperService.delete(id);
        return R.ok();
    }

    @DeleteMapping("/batch")
    @Operation(summary = "批量删除试卷")
    public R<Void> deleteBatch(@RequestBody Map<String, List<Long>> request) {
        paperService.deleteBatch(request.get("ids"));
        return R.ok();
    }
}
