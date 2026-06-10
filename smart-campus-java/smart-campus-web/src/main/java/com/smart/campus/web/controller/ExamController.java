package com.smart.campus.web.controller;

import com.campus.result.R;
import com.smart.campus.web.biz.ExamWebBiz;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 考试控制器（学生端）
 */
@RestController
@RequestMapping("/exams")
@Validated
@Tag(name = "考试")
public class ExamController {

    private final ExamWebBiz examWebBiz;

    public ExamController(ExamWebBiz examWebBiz) {
        this.examWebBiz = examWebBiz;
    }

    @GetMapping("/my")
    @Operation(summary = "我的考试列表")
    public R<List<Map<String, Object>>> getMyExams(
            @RequestAttribute(required = false) Long studentId) {
        return R.ok(examWebBiz.getMyExams(studentId));
    }

    @GetMapping("/{id}")
    @Operation(summary = "考试详情")
    public R<Map<String, Object>> getDetail(@PathVariable String id) {
        return R.ok(examWebBiz.getDetail(id));
    }

    @PostMapping("/start")
    @Operation(summary = "开始考试")
    public R<Map<String, Object>> startExam(
            @RequestAttribute(required = false) Long studentId,
            @RequestBody Map<String, String> body) {
        String examId = body.get("examId");
        if (examId == null || examId.isBlank()) {
            return R.fail("考试ID不能为空");
        }
        return R.ok(examWebBiz.startExam(studentId, examId));
    }

    @GetMapping("/results")
    @Operation(summary = "成绩查询")
    public R<List<Map<String, Object>>> getExamResults(
            @RequestAttribute(required = false) Long studentId) {
        return R.ok(examWebBiz.getExamResults(studentId));
    }

    @PostMapping("/submit")
    @Operation(summary = "提交答卷")
    public R<Map<String, Object>> submitAnswers(
            @RequestAttribute(required = false) Long studentId,
            @RequestBody Map<String, Object> body) {
        String examId = (String) body.get("examId");
        Map<String, String> answers = new LinkedHashMap<>();
        if (body.containsKey("answers") && body.get("answers") instanceof Map) {
            Map<?, ?> raw = (Map<?, ?>) body.get("answers");
            raw.forEach((k, v) -> answers.put(String.valueOf(k), String.valueOf(v)));
        }
        return R.ok(examWebBiz.submitAnswers(studentId, examId, answers));
    }
}
