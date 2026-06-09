package com.smart.campus.admin.controller;

import com.campus.exception.UnauthorizedException;
import com.campus.result.R;
import com.campus.entity.Course;
import com.smart.campus.admin.service.impl.StudentCourseServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 选课 Controller
 */
@Tag(name = "选课管理")
@RestController
@RequestMapping("/student-courses")
public class StudentCourseController {

    private final StudentCourseServiceImpl studentCourseService;

    public StudentCourseController(StudentCourseServiceImpl studentCourseService) {
        this.studentCourseService = studentCourseService;
    }

    private static Long requireStudentId(Long studentId) {
        if (studentId == null) {
            throw new UnauthorizedException("请使用学生账号登录，或重新登录以刷新身份信息");
        }
        return studentId;
    }

    @Operation(summary = "学生选课")
    @PostMapping("/enroll")
    public R<Void> enroll(@RequestAttribute(required = false) Long studentId,
                          @RequestBody Map<String, Long> body) {
        Long courseId = body.get("courseId");
        if (courseId == null) {
            return R.fail("课程ID不能为空");
        }
        studentCourseService.enroll(requireStudentId(studentId), courseId);
        return R.ok();
    }

    @Operation(summary = "退课")
    @PostMapping("/drop/{courseId}")
    public R<Void> drop(@RequestAttribute(required = false) Long studentId,
                        @PathVariable Long courseId) {
        studentCourseService.drop(requireStudentId(studentId), courseId);
        return R.ok();
    }

    @Operation(summary = "获取我的课程列表")
    @GetMapping("/my")
    public R<List<Course>> myCourses(@RequestAttribute(required = false) Long studentId) {
        List<Course> courses = studentCourseService.getMyCourses(requireStudentId(studentId));
        return R.ok(courses);
    }

    @Operation(summary = "检查是否已选某课程")
    @GetMapping("/check/{courseId}")
    public R<Boolean> check(@RequestAttribute(required = false) Long studentId,
                            @PathVariable Long courseId) {
        boolean enrolled = studentCourseService.isEnrolled(requireStudentId(studentId), courseId);
        return R.ok(enrolled);
    }

    @Operation(summary = "获取已选课程数量")
    @GetMapping("/count")
    public R<Long> count(@RequestAttribute(required = false) Long studentId) {
        long count = studentCourseService.countByStudentId(requireStudentId(studentId));
        return R.ok(count);
    }
}
