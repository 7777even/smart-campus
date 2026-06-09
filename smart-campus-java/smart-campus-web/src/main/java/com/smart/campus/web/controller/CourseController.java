package com.smart.campus.web.controller;

import com.campus.entity.PageResult;
import com.campus.result.R;
import com.smart.campus.web.biz.CourseWebBiz;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 课程控制器（学生端）
 */
@RestController
@RequestMapping("/courses")
@Validated
@Tag(name = "课程")
public class CourseController {

    private final CourseWebBiz courseWebBiz;

    public CourseController(CourseWebBiz courseWebBiz) {
        this.courseWebBiz = courseWebBiz;
    }

    @GetMapping("/page")
    @Operation(summary = "分页查询课程列表")
    public R<PageResult<Map<String, Object>>> page(
            @RequestParam(defaultValue = "1") int pageNo,
            @RequestParam(defaultValue = "15") int pageSize,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Long departmentId,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) Integer credit) {
        Map<String, Object> params = new LinkedHashMap<>();
        params.put("keyword", keyword);
        params.put("departmentId", departmentId);
        params.put("type", type);
        params.put("credit", credit);
        return R.ok(courseWebBiz.pageList(pageNo, pageSize, params));
    }

    @GetMapping("/{id}")
    @Operation(summary = "课程详情")
    public R<Map<String, Object>> getDetail(@PathVariable String id) {
        return R.ok(courseWebBiz.getDetail(id));
    }

    @GetMapping("/hot")
    @Operation(summary = "热门课程列表")
    public R<List<Map<String, Object>>> getHotCourses(
            @RequestParam(defaultValue = "8") int limit) {
        return R.ok(courseWebBiz.getHotCourses(limit));
    }

    @GetMapping("/my")
    @Operation(summary = "我的课程列表")
    public R<List<Map<String, Object>>> getMyCourses(
            @RequestAttribute(required = false) Long studentId) {
        return R.ok(courseWebBiz.getMyCourses(studentId));
    }

    @GetMapping("/schedule")
    @Operation(summary = "我的课表")
    public R<List<Map<String, Object>>> getSchedule(
            @RequestAttribute(required = false) Long studentId) {
        return R.ok(courseWebBiz.getSchedule(studentId));
    }

    @GetMapping("/enrolled/{courseId}/check")
    @Operation(summary = "检查是否已选某课程")
    public R<Boolean> checkEnrolled(
            @RequestAttribute(required = false) Long studentId,
            @PathVariable String courseId) {
        return R.ok(courseWebBiz.checkEnrolled(studentId, courseId));
    }
}
