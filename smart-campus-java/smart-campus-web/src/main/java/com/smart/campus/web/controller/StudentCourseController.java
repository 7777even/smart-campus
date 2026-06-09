package com.smart.campus.web.controller;

import com.campus.exception.UnauthorizedException;
import com.campus.result.R;
import com.smart.campus.web.biz.StudentCourseWebBiz;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 选课控制器（学生端）
 */
@RestController
@RequestMapping("/student-courses")
@Validated
@Tag(name = "选课")
public class StudentCourseController {

    private final StudentCourseWebBiz studentCourseWebBiz;

    public StudentCourseController(StudentCourseWebBiz studentCourseWebBiz) {
        this.studentCourseWebBiz = studentCourseWebBiz;
    }

    private static Long requireStudentId(Long studentId) {
        if (studentId == null) {
            throw new UnauthorizedException("请先登录");
        }
        return studentId;
    }

    @PostMapping("/enroll")
    @Operation(summary = "学生选课")
    public R<Void> enroll(
            @RequestAttribute(required = false) Long studentId,
            @RequestBody Map<String, String> body) {
        String courseId = body.get("courseId");
        if (courseId == null || courseId.isBlank()) {
            return R.fail("课程ID不能为空");
        }
        studentCourseWebBiz.enroll(requireStudentId(studentId), courseId);
        return R.ok();
    }

    @PostMapping("/drop/{courseId}")
    @Operation(summary = "学生退课")
    public R<Void> drop(
            @RequestAttribute(required = false) Long studentId,
            @PathVariable String courseId) {
        studentCourseWebBiz.drop(requireStudentId(studentId), courseId);
        return R.ok();
    }
}
