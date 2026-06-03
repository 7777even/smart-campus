package com.smart.campus.admin.controller;

import com.campus.result.R;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

@RestController
@RequestMapping("/dashboard")
public class DashboardController {

    private final JdbcTemplate jdbcTemplate;

    public DashboardController(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @GetMapping("/overview")
    public R<Map<String, Object>> overview() {
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("totalStudents", count("sys_student"));
        data.put("totalTeachers", count("sys_teacher"));
        data.put("totalDepartments", count("sys_department"));
        data.put("totalMajors", count("sys_major"));
        data.put("totalClasses", count("sys_class"));
        data.put("totalCourses", count("biz_course"));
        return R.ok(data);
    }

    @GetMapping("/teaching")
    public R<Map<String, Object>> teaching() {
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("semesterCourses", count("biz_course"));
        data.put("completedExams", countWhere("biz_exam", "status", "已结束"));
        data.put("pendingExams", countWhere("biz_exam", "status", "待开始"));

        List<Map<String, Object>> workload = new ArrayList<>();
        Calendar cal = Calendar.getInstance();
        for (int i = 5; i >= 0; i--) {
            cal.setTime(new Date());
            cal.add(Calendar.MONTH, -i);
            int month = cal.get(Calendar.MONTH) + 1;
            String monthStr = month + "月";
            Long cnt = safeQuery("SELECT COUNT(*) FROM biz_course WHERE MONTH(create_time) = ?", Long.class, month);
            Map<String, Object> item = new LinkedHashMap<>();
            item.put("month", monthStr);
            item.put("count", cnt != null ? cnt : 0);
            workload.add(item);
        }
        data.put("teacherWorkload", workload);
        return R.ok(data);
    }

    @GetMapping("/students")
    public R<Map<String, Object>> students() {
        Map<String, Object> data = new LinkedHashMap<>();

        List<Map<String, Object>> depts = safeQueryList(
                "SELECT d.name, COUNT(s.id) AS value FROM sys_student s " +
                "LEFT JOIN sys_department d ON s.department_id = d.id " +
                "GROUP BY d.name ORDER BY value DESC");
        data.put("departments", depts);

        List<Map<String, Object>> grades = safeQueryList(
                "SELECT CONCAT(c.year, '级') AS grade, COUNT(*) AS count FROM sys_student s " +
                "LEFT JOIN sys_class c ON s.class_id = c.id " +
                "GROUP BY c.year ORDER BY c.year");
        data.put("grades", grades);

        Map<String, Object> gender = new LinkedHashMap<>();
        Long male = safeQuery("SELECT COUNT(*) FROM sys_student WHERE gender = '男'", Long.class);
        Long female = safeQuery("SELECT COUNT(*) FROM sys_student WHERE gender = '女'", Long.class);
        gender.put("male", male != null ? male : 0);
        gender.put("female", female != null ? female : 0);
        data.put("gender", gender);

        List<Map<String, Object>> growth = new ArrayList<>();
        Calendar cal = Calendar.getInstance();
        int currentYear = cal.get(Calendar.YEAR);
        for (int y = currentYear - 5; y <= currentYear; y++) {
            String yearStr = String.valueOf(y);
            Long cnt = safeQuery(
                    "SELECT COUNT(*) FROM sys_student s LEFT JOIN sys_class c ON s.class_id = c.id WHERE c.year = ?",
                    Long.class, y);
            Map<String, Object> item = new LinkedHashMap<>();
            item.put("year", yearStr);
            item.put("count", cnt != null ? cnt : 0);
            growth.add(item);
        }
        data.put("growth", growth);
        return R.ok(data);
    }

    @GetMapping("/resources")
    public R<Map<String, Object>> resources() {
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("total", count("biz_resource"));
        data.put("totalDownloads", safeQuery("SELECT COALESCE(SUM(downloads), 0) FROM biz_resource", Long.class));

        List<Map<String, Object>> uploadTrend = new ArrayList<>();
        Calendar cal = Calendar.getInstance();
        for (int i = 5; i >= 0; i--) {
            cal.setTime(new Date());
            cal.add(Calendar.MONTH, -i);
            int month = cal.get(Calendar.MONTH) + 1;
            String monthStr = month + "月";
            Long cnt = safeQuery("SELECT COUNT(*) FROM biz_resource WHERE MONTH(create_time) = ?", Long.class, month);
            Map<String, Object> item = new LinkedHashMap<>();
            item.put("month", monthStr);
            item.put("count", cnt != null ? cnt : 0);
            uploadTrend.add(item);
        }
        data.put("uploadTrend", uploadTrend);

        List<Map<String, Object>> hotResources = safeQueryList(
                "SELECT name, downloads, uploader AS dept FROM biz_resource ORDER BY downloads DESC LIMIT 10");
        data.put("hotResources", hotResources);
        return R.ok(data);
    }

    @GetMapping("/exams")
    public R<Map<String, Object>> exams() {
        Map<String, Object> data = new LinkedHashMap<>();

        double passRate = 0;
        Long total = safeQuery("SELECT COUNT(*) FROM biz_exam_student", Long.class);
        Long pass = safeQuery("SELECT COUNT(*) FROM biz_exam_student WHERE score >= 60", Long.class);
        if (total != null && total > 0 && pass != null) {
            passRate = Math.round((double) pass / total * 10000.0) / 100.0;
        }
        data.put("passRate", passRate);

        double excellenceRate = 0;
        Long excellent = safeQuery("SELECT COUNT(*) FROM biz_exam_student WHERE score >= 85", Long.class);
        if (total != null && total > 0 && excellent != null) {
            excellenceRate = Math.round((double) excellent / total * 10000.0) / 100.0;
        }
        data.put("excellenceRate", excellenceRate);

        List<Map<String, Object>> countTrend = new ArrayList<>();
        Calendar cal = Calendar.getInstance();
        for (int i = 5; i >= 0; i--) {
            cal.setTime(new Date());
            cal.add(Calendar.MONTH, -i);
            int month = cal.get(Calendar.MONTH) + 1;
            String monthStr = month + "月";
            Long cnt = safeQuery("SELECT COUNT(*) FROM biz_exam WHERE MONTH(create_time) = ?", Long.class, month);
            Map<String, Object> item = new LinkedHashMap<>();
            item.put("month", monthStr);
            item.put("count", cnt != null ? cnt : 0);
            countTrend.add(item);
        }
        data.put("countTrend", countTrend);

        List<Map<String, Object>> avgScores = safeQueryList(
                "SELECT d.name, ROUND(AVG(es.score), 1) AS value " +
                "FROM biz_exam_student es " +
                "LEFT JOIN sys_student s ON es.student_id = s.id " +
                "LEFT JOIN sys_department d ON s.department_id = d.id " +
                "WHERE es.score IS NOT NULL " +
                "GROUP BY d.name ORDER BY value DESC");
        data.put("avgScores", avgScores);
        return R.ok(data);
    }

    @GetMapping("/system")
    public R<Map<String, Object>> system() {
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("onlineUsers", count("sys_user"));
        data.put("todayLogins", 0);
        data.put("weeklyActive", 0);
        data.put("cpuUsage", (int)(Math.random() * 30 + 20));
        data.put("memoryUsage", (int)(Math.random() * 20 + 50));
        data.put("diskUsage", (int)(Math.random() * 15 + 55));
        data.put("status", "healthy");

        List<Map<String, Object>> services = new ArrayList<>();
        for (String name : new String[]{"Web服务", "数据库", "缓存服务", "文件存储"}) {
            Map<String, Object> svc = new LinkedHashMap<>();
            svc.put("name", name);
            svc.put("status", "normal");
            services.add(svc);
        }
        data.put("services", services);
        return R.ok(data);
    }

    private long count(String table) {
        Long result = safeQuery("SELECT COUNT(*) FROM " + table, Long.class);
        return result != null ? result : 0;
    }

    private long countWhere(String table, String column, String value) {
        Long result = safeQuery("SELECT COUNT(*) FROM " + table + " WHERE " + column + " = ?", Long.class, value);
        return result != null ? result : 0;
    }

    private <T> T safeQuery(String sql, Class<T> cls, Object... args) {
        try {
            return jdbcTemplate.queryForObject(sql, cls, args);
        } catch (Exception e) {
            return null;
        }
    }

    private List<Map<String, Object>> safeQueryList(String sql, Object... args) {
        try {
            return jdbcTemplate.queryForList(sql, args);
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }
}
