package com.smart.campus.admin.biz;

import com.campus.exception.UnauthorizedException;
import org.springframework.stereotype.Component;

/**
 * 选课业务（管理端）
 */
@Component
public class StudentCourseAdminBiz {

    public static Long requireStudentId(Long studentId) {
        if (studentId == null) {
            throw new UnauthorizedException("请使用学生账号登录，或重新登录以刷新身份信息");
        }
        return studentId;
    }
}
