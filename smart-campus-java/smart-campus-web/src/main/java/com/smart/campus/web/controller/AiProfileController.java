package com.smart.campus.web.controller;

import com.campus.result.R;
import com.smart.campus.web.biz.AiProfileWebBiz;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * AI 画像控制器（学生端）
 */
@RestController
@RequestMapping("/ai/profile")
@Validated
@Tag(name = "AI画像")
public class AiProfileController {

    private final AiProfileWebBiz aiProfileWebBiz;

    public AiProfileController(AiProfileWebBiz aiProfileWebBiz) {
        this.aiProfileWebBiz = aiProfileWebBiz;
    }

    @GetMapping("/student/{studentId}")
    @Operation(summary = "获取学生画像")
    public R<Map<String, Object>> getProfile(@PathVariable Long studentId) {
        return R.ok(aiProfileWebBiz.getProfile(studentId));
    }

    @GetMapping("/warning/student/{studentId}")
    @Operation(summary = "获取学业预警")
    public R<List<Map<String, Object>>> getWarnings(@PathVariable Long studentId) {
        return R.ok(aiProfileWebBiz.getWarnings(studentId));
    }
}
