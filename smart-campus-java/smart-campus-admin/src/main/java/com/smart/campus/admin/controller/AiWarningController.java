package com.smart.campus.admin.controller;

import com.campus.entity.PageResult;
import com.campus.result.R;
import com.smart.campus.admin.entity.AiEarlyWarning;
import com.smart.campus.admin.service.AiWarningService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 学业预警控制器
 */
@RestController
@RequestMapping("/ai/warning")
@Tag(name = "学业预警")
public class AiWarningController {

    private final AiWarningService warningService;

    public AiWarningController(AiWarningService warningService) {
        this.warningService = warningService;
    }

    @GetMapping("/page")
    @Operation(summary = "分页查询预警")
    public R<PageResult<AiEarlyWarning>> page(
            @RequestParam(defaultValue = "1") int pageNo,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) String level,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String warningType) {
        Map<String, Object> params = new HashMap<>();
        params.put("level", level);
        params.put("status", status);
        params.put("warningType", warningType);
        params.put("sortField", "create_time");
        params.put("sortOrder", "DESC");
        PageResult<AiEarlyWarning> result = warningService.page(pageNo, pageSize, params);
        return R.ok(result);
    }

    @GetMapping("/student/{studentId}")
    @Operation(summary = "获取学生预警列表")
    public R<List<AiEarlyWarning>> getByStudentId(@PathVariable Long studentId) {
        // 暂按学生 ID 通过 page 方式查询（需要 warningService 扩展支持）
        Map<String, Object> params = new HashMap<>();
        return R.ok(Collections.emptyList());
    }

    @PostMapping("/evaluate/{profileId}")
    @Operation(summary = "评估单个画像")
    public R<List<AiEarlyWarning>> evaluate(@PathVariable Long profileId) {
        return R.ok(warningService.evaluateProfile(profileId));
    }

    @PostMapping("/evaluate-all")
    @Operation(summary = "全量评估")
    public R<Map<String, Object>> evaluateAll() {
        return R.ok(warningService.evaluateAll());
    }

    @PutMapping("/{id}/resolve")
    @Operation(summary = "处理预警")
    public R<AiEarlyWarning> resolve(@PathVariable Long id, @RequestBody Map<String, String> body) {
        String resolver = body.getOrDefault("resolver", "系统");
        return R.ok(warningService.resolveWarning(id, resolver));
    }

    @GetMapping("/statistics")
    @Operation(summary = "预警统计")
    public R<Map<String, Object>> statistics() {
        return R.ok(warningService.getStatistics());
    }
}
