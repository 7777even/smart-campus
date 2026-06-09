package com.smart.campus.admin.controller;

import com.campus.entity.PageResult;
import com.campus.result.R;
import com.campus.entity.AiStudentProfile;
import com.smart.campus.admin.service.AiProfileService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 学业画像控制器
 */
@RestController
@RequestMapping("/ai/profile")
@Tag(name = "学业画像")
public class AiProfileController {

    private final AiProfileService profileService;

    public AiProfileController(AiProfileService profileService) {
        this.profileService = profileService;
    }

    @GetMapping("/page")
    @Operation(summary = "分页查询学业画像")
    public R<PageResult<AiStudentProfile>> page(
            @RequestParam(defaultValue = "1") int pageNo,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) String riskLevel,
            @RequestParam(required = false) Long departmentId,
            @RequestParam(required = false) String keyword) {
        Map<String, Object> params = new HashMap<>();
        params.put("riskLevel", riskLevel);
        params.put("departmentId", departmentId);
        params.put("keyword", keyword);
        PageResult<AiStudentProfile> result = profileService.page(pageNo, pageSize, params);
        return R.ok(result);
    }

    @GetMapping("/student/{studentId}")
    @Operation(summary = "获取学生画像")
    public R<AiStudentProfile> getByStudentId(@PathVariable Long studentId) {
        // 如果画像不存在则自动计算
        AiStudentProfile profile = profileService.calculateProfile(studentId);
        return R.ok(profile);
    }

    @PostMapping("/calculate/{studentId}")
    @Operation(summary = "手动计算单个画像")
    public R<AiStudentProfile> calculate(@PathVariable Long studentId) {
        return R.ok(profileService.calculateProfile(studentId));
    }

    @PostMapping("/calculate-all")
    @Operation(summary = "全量计算画像")
    public R<Map<String, Object>> calculateAll() {
        int count = profileService.calculateAllProfiles();
        return R.ok("成功计算 " + count + " 个学生画像", Map.of("count", count));
    }

    @GetMapping("/statistics")
    @Operation(summary = "画像统计")
    public R<Map<String, Object>> statistics() {
        return R.ok(profileService.getStatistics());
    }
}
