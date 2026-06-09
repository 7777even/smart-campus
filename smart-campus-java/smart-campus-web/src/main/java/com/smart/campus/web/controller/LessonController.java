package com.smart.campus.web.controller;

import com.campus.result.R;
import com.smart.campus.web.biz.ChapterWebBiz;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 课时控制器（学生端）
 */
@RestController
@RequestMapping("/lessons")
@Validated
@Tag(name = "课时")
public class LessonController {

    private final ChapterWebBiz chapterWebBiz;

    public LessonController(ChapterWebBiz chapterWebBiz) {
        this.chapterWebBiz = chapterWebBiz;
    }

    @GetMapping("/by-chapter/{chapterId}")
    @Operation(summary = "获取章节下的课时列表")
    public R<List<Map<String, Object>>> getByChapter(
            @PathVariable String chapterId) {
        return R.ok(chapterWebBiz.getLessonsByChapterId(chapterId));
    }

    @GetMapping("/{id}")
    @Operation(summary = "课时详情")
    public R<Map<String, Object>> getDetail(@PathVariable String id) {
        return R.ok(chapterWebBiz.getLessonDetail(id));
    }

    @GetMapping("/{id}/resources")
    @Operation(summary = "课时关联的资源列表")
    public R<List<Map<String, Object>>> getResources(@PathVariable String id) {
        return R.ok(chapterWebBiz.getResourcesByLessonId(id));
    }
}
