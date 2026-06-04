package com.smart.campus.admin.controller;

import com.campus.result.R;
import com.smart.campus.admin.service.RecommendService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 推荐 Controller — 个性化课程/资源推荐
 */
@Tag(name = "推荐管理")
@RestController
@RequestMapping("/recommend")
public class RecommendController {

    private final RecommendService recommendService;

    public RecommendController(RecommendService recommendService) {
        this.recommendService = recommendService;
    }

    @Operation(summary = "个性化课程推荐（登录用户）")
    @GetMapping("/courses")
    public R<List<Map<String, Object>>> recommendCourses(
            @RequestAttribute(required = false) Long studentId,
            @RequestParam(defaultValue = "8") int limit) {
        if (studentId == null) {
            return R.ok(recommendService.recommendCoursesForGuest(limit));
        }
        return R.ok(recommendService.recommendCourses(studentId, limit));
    }

    @Operation(summary = "热门课程推荐（免登录）")
    @GetMapping("/hot-courses")
    public R<List<Map<String, Object>>> hotCourses(
            @RequestParam(defaultValue = "8") int limit) {
        return R.ok(recommendService.hotCourses(limit));
    }

    @Operation(summary = "个性化资源推荐")
    @GetMapping("/resources")
    public R<List<Map<String, Object>>> recommendResources(
            @RequestAttribute(required = false) Long studentId,
            @RequestParam(defaultValue = "8") int limit) {
        if (studentId == null) {
            return R.ok(recommendService.hotCourses(limit)); // fallback
        }
        return R.ok(recommendService.recommendResources(studentId, limit));
    }

    @Operation(summary = "同学也在学")
    @GetMapping("/peers-also-enrolled/{courseId}")
    public R<List<Map<String, Object>>> peersAlsoEnrolled(
            @PathVariable Long courseId,
            @RequestParam(defaultValue = "6") int limit) {
        return R.ok(recommendService.peersAlsoEnrolled(courseId, limit));
    }
}
