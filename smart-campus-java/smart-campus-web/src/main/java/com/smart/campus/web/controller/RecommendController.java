package com.smart.campus.web.controller;

import com.campus.result.R;
import com.smart.campus.web.biz.RecommendWebBiz;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 推荐控制器（学生端）
 */
@RestController
@RequestMapping("/recommend")
@Validated
@Tag(name = "推荐")
public class RecommendController {

    private final RecommendWebBiz recommendWebBiz;

    public RecommendController(RecommendWebBiz recommendWebBiz) {
        this.recommendWebBiz = recommendWebBiz;
    }

    @GetMapping("/courses")
    @Operation(summary = "个性化课程推荐")
    public R<List<Map<String, Object>>> getCourses(
            @RequestAttribute(required = false) Long studentId,
            @RequestParam(defaultValue = "8") int limit) {
        if (studentId == null) {
            return R.ok(recommendWebBiz.getCoursesForGuest(limit));
        }
        return R.ok(recommendWebBiz.getCourses(studentId, limit));
    }

    @GetMapping("/hot-courses")
    @Operation(summary = "热门课程推荐")
    public R<List<Map<String, Object>>> getHotCourses(
            @RequestParam(defaultValue = "8") int limit) {
        return R.ok(recommendWebBiz.getHotCourses(limit));
    }

    @GetMapping("/resources")
    @Operation(summary = "个性化资源推荐")
    public R<List<Map<String, Object>>> getResources(
            @RequestAttribute(required = false) Long studentId,
            @RequestParam(defaultValue = "8") int limit) {
        if (studentId == null) {
            return R.ok(recommendWebBiz.getHotResources(limit));
        }
        return R.ok(recommendWebBiz.getResources(studentId, limit));
    }

    @GetMapping("/peers-also-enrolled/{courseId}")
    @Operation(summary = "同学也在学")
    public R<List<Map<String, Object>>> peersAlsoEnrolled(
            @PathVariable String courseId,
            @RequestParam(defaultValue = "6") int limit) {
        return R.ok(recommendWebBiz.peersAlsoEnrolled(courseId, limit));
    }
}
