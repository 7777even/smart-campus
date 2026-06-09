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
 * 章节控制器（学生端）
 */
@RestController
@RequestMapping("/chapters")
@Validated
@Tag(name = "章节")
public class ChapterController {

    private final ChapterWebBiz chapterWebBiz;

    public ChapterController(ChapterWebBiz chapterWebBiz) {
        this.chapterWebBiz = chapterWebBiz;
    }

    @GetMapping("/by-course/{courseId}")
    @Operation(summary = "获取课程的章节与课时列表")
    public R<List<Map<String, Object>>> getByCourse(
            @PathVariable String courseId) {
        return R.ok(chapterWebBiz.getByCourseId(courseId));
    }
}
