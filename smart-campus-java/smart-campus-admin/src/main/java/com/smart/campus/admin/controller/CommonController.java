package com.smart.campus.admin.controller;

import com.campus.result.R;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.*;

/**
 * 通用数据接口 — 提供下拉选择框数据
 */
@RestController
@RequestMapping("/common")
public class CommonController {

    private final JdbcTemplate jdbcTemplate;

    public CommonController(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    /**
     * 所有院系
     */
    @GetMapping("/departments")
    public R<List<Map<String, Object>>> departments() {
        try {
            List<Map<String, Object>> list = jdbcTemplate.queryForList(
                    "SELECT id, name FROM sys_department ORDER BY sort ASC");
            return R.ok(list);
        } catch (Exception e) {
            return R.ok(Collections.emptyList());
        }
    }

    /**
     * 根据院系查询专业
     */
    @GetMapping("/majors")
    public R<List<Map<String, Object>>> majors(@RequestParam(required = false) Long departmentId) {
        try {
            if (departmentId != null) {
                List<Map<String, Object>> list = jdbcTemplate.queryForList(
                        "SELECT id, name FROM sys_major WHERE department_id = ? ORDER BY name",
                        departmentId);
                return R.ok(list);
            } else {
                List<Map<String, Object>> list = jdbcTemplate.queryForList(
                        "SELECT id, name FROM sys_major ORDER BY name");
                return R.ok(list);
            }
        } catch (Exception e) {
            return R.ok(Collections.emptyList());
        }
    }

    /**
     * 所有教师
     */
    @GetMapping("/teachers")
    public R<List<Map<String, Object>>> teachers() {
        try {
            List<Map<String, Object>> list = jdbcTemplate.queryForList(
                    "SELECT id, name FROM sys_teacher ORDER BY name");
            return R.ok(list);
        } catch (Exception e) {
            return R.ok(Collections.emptyList());
        }
    }

    /**
     * 所有班级
     */
    @GetMapping("/classes")
    public R<List<Map<String, Object>>> classes() {
        try {
            List<Map<String, Object>> list = jdbcTemplate.queryForList(
                    "SELECT id, name FROM sys_class ORDER BY name");
            return R.ok(list);
        } catch (Exception e) {
            return R.ok(Collections.emptyList());
        }
    }

    /**
     * 所有课程
     */
    @GetMapping("/courses")
    public R<List<Map<String, Object>>> courses() {
        try {
            List<Map<String, Object>> list = jdbcTemplate.queryForList(
                    "SELECT id, name FROM biz_course ORDER BY name");
            return R.ok(list);
        } catch (Exception e) {
            return R.ok(Collections.emptyList());
        }
    }
}
