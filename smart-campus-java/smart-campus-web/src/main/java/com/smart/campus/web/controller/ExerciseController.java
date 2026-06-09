package com.smart.campus.web.controller;

import com.campus.result.R;
import com.smart.campus.web.biz.ExerciseWebBiz;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 习题控制器（学生端）
 */
@RestController
@RequestMapping("/exercises")
@Validated
@Tag(name = "习题")
public class ExerciseController {

    private final ExerciseWebBiz exerciseWebBiz;

    public ExerciseController(ExerciseWebBiz exerciseWebBiz) {
        this.exerciseWebBiz = exerciseWebBiz;
    }

    @GetMapping("/by-course/{courseId}")
    @Operation(summary = "获取课程下的习题列表（不显示答案）")
    public R<List<Map<String, Object>>> listByCourse(@PathVariable String courseId) {
        return R.ok(exerciseWebBiz.listByCourse(courseId));
    }

    @GetMapping("/{id}")
    @Operation(summary = "习题详情（显示答案）")
    public R<Map<String, Object>> getDetail(@PathVariable String id) {
        return R.ok(exerciseWebBiz.getDetail(id));
    }

    @PostMapping("/{id}/submit")
    @Operation(summary = "提交习题答案")
    public R<Map<String, Object>> submitAnswer(
            @PathVariable String id,
            @RequestBody Map<String, String> body) {
        String answer = body.get("answer");
        return R.ok(exerciseWebBiz.submitAnswer(id, answer));
    }
}
