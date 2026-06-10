package com.smart.campus.web.controller;

import com.campus.result.R;
import com.smart.campus.web.biz.LearningWebBiz;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 学习进度控制器（学生端）
 */
@RestController
@RequestMapping("/learning")
@Validated
@Tag(name = "学习进度")
public class LearningController {

    private final LearningWebBiz learningWebBiz;

    public LearningController(LearningWebBiz learningWebBiz) {
        this.learningWebBiz = learningWebBiz;
    }

    @GetMapping("/progress")
    @Operation(summary = "获取课程学习进度")
    public R<Map<String, Object>> getProgress(
            @RequestAttribute(required = false) Long studentId,
            @RequestParam String courseId) {
        if (courseId == null || courseId.isBlank()) {
            return R.fail("课程ID不能为空");
        }
        return R.ok(learningWebBiz.getProgress(studentId, courseId));
    }

    @PostMapping("/progress/record")
    @Operation(summary = "记录学习进度")
    public R<Void> recordProgress(
            @RequestAttribute(required = false) Long studentId,
            @RequestBody Map<String, Object> body) {
        String courseId = (String) body.get("courseId");
        String lessonId = (String) body.get("lessonId");
        Integer progress = body.get("progress") != null ? ((Number) body.get("progress")).intValue() : 0;
        learningWebBiz.recordProgress(studentId, courseId, lessonId, progress);
        return R.ok();
    }

    @GetMapping("/video-progress")
    @Operation(summary = "获取视频播放进度")
    public R<Map<String, Object>> getVideoProgress(
            @RequestAttribute(required = false) Long studentId,
            @RequestParam String lessonId) {
        return R.ok(learningWebBiz.getVideoProgress(studentId, lessonId));
    }

    @GetMapping("/logs")
    @Operation(summary = "获取学习日志")
    public R<Map<String, Object>> getLogs(
            @RequestAttribute(required = false) Long studentId,
            @RequestParam(defaultValue = "0") int pageNo,
            @RequestParam(defaultValue = "10") int pageSize) {
        return R.ok(learningWebBiz.getLogs(studentId, pageNo, pageSize));
    }

    @PostMapping("/video-progress/record")
    @Operation(summary = "记录视频播放进度")
    public R<Void> recordVideoProgress(
            @RequestAttribute(required = false) Long studentId,
            @RequestBody Map<String, Object> body) {
        String lessonId = (String) body.get("lessonId");
        Double playTime = body.get("playTime") != null ? ((Number) body.get("playTime")).doubleValue() : 0.0;
        learningWebBiz.recordVideoProgress(studentId, lessonId, playTime);
        return R.ok();
    }
}
